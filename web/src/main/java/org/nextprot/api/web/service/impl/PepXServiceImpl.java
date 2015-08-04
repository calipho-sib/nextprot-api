package org.nextprot.api.web.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PepXServiceImpl implements PepXService {

	private static final Log LOGGER = LogFactory.getLog(PepXServiceImpl.class);

	@Autowired
	private EntryBuilderService entryBuilderService;

	private String pepXUrl;

	@Value("${pepx.url}")
	public void setPepXUrl(String pepXUrl) {
		this.pepXUrl = pepXUrl;
	}

	@Override
	public List<Entry> findEntriesWithPeptides(String peptide, boolean modeIsoleucine) {

		List<Entry> entries = new ArrayList<Entry>();

		Map<String, List<Pair<Integer, Integer>>> entriesMap = getPepXResponse(peptide, modeIsoleucine);

		Set<String> entriesNames = entriesMap.keySet();
		for (String entryName : entriesNames) {
			EntryConfig targetIsoconf = EntryConfig.newConfig(entryName).withTargetIsoforms().with("variant").withoutAdditionalReferences().withoutProperties(); // .with("variant")
			Entry entry = entryBuilderService.build(targetIsoconf);
			Entry resultEntry = filterVariantAnnotsWithPepxResults(peptide, modeIsoleucine, entry.getUniqueName(), entry.getAnnotations(), entriesMap.get(entryName));
			if((resultEntry.getAnnotations() != null) && (!resultEntry.getAnnotations().isEmpty())){
				entries.add(resultEntry);
			}
		}

		return entries;

	}

	private Map<String, List<Pair<Integer, Integer>>> getPepXResponse(String peptide, boolean modeIsoleucine) {

		Map<String, List<Pair<Integer, Integer>>> entriesMap = new HashMap<>();
		String httpRequest = pepXUrl + (modeIsoleucine ? ("?mode=IL&pep=" + peptide) : ("?pep=" + peptide));

		try {

			URL pepXUrl = new URL(httpRequest);
			URLConnection px = pepXUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(px.getInputStream()));
			String inputLine;
			StringBuilder sb = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}

			String[] acs = sb.toString().split("<br>");
			NPreconditions.checkTrue(acs[0].toLowerCase().contains("searching"), "Unexpected format from pepX on the first row: " + acs[0]);
			for (int i = 1; i < acs.length - 1; i++) { // Do not take 1st and
														// last row
				String[] ei = acs[i].split("-");
				String currentEntry = ei[0];
				int isoNumber = Integer.valueOf(ei[1]);
				if (ei.length == 2 || ei.length == 3) {
					if (!entriesMap.containsKey(currentEntry)) {
						entriesMap.put(currentEntry, new ArrayList<Pair<Integer, Integer>>());
					}
					Integer position = (ei.length == 3) ? (Integer.valueOf(ei[2])) : null;
					entriesMap.get(ei[0]).add(new Pair<Integer, Integer>(isoNumber, position));
				} else {
					throw new NextProtException("Unexpected format from pepX on row " + i + ": " + acs[i]);
				}
			}

			in.close();

		} catch (MalformedURLException e) {
			throw new NextProtException(e);
		} catch (IOException e) {
			throw new NextProtException(e);
		}

		return entriesMap;
	}

	private Entry filterVariantAnnotsWithPepxResults(String peptide, boolean modeIsoleucine, String uniqueName, List<Annotation> annotations, List<Pair<Integer, Integer>> isoformNamesAndOptionalPosition) {

		Entry resultEntry = new Entry(uniqueName);
		
		List<Annotation> finalAnnotations = new ArrayList<>();
		for (Pair<Integer, Integer> isos : isoformNamesAndOptionalPosition) {
			String isoformName = uniqueName + "-" + isos.getFirst();

			Annotation annotation = new Annotation();
			annotation.setCategoryOnly("pepx-virtual-annotation");
			annotation.setCvTermName(peptide);
			annotation.setDescription("This virtual annotation describes the peptide " + peptide + " found in " + isoformName);

			IsoformSpecificity is = new IsoformSpecificity(null, isoformName);
			if (isos.getSecond() != null) {// It means there is a variant!!!

				int startPeptidePosition = isos.getSecond();
				int endPeptidePosition = startPeptidePosition + peptide.length();

				List<Annotation> variantAnnotations = AnnotationUtils.filterAnnotationsBetweenPositions(startPeptidePosition, endPeptidePosition, annotations, isoformName);

				if ((variantAnnotations == null) || variantAnnotations.isEmpty()) {
					LOGGER.warn("PepX returned a variant that we do not consider a cosmic variant for isoform " + isoformName + " at position" + startPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);
					continue; //WILL NOT ADD THIS ANNOTATION
				} else if (variantAnnotations.size() > 1) {
					LOGGER.warn("PepX returned more than 1 variant for isoform " + isoformName + " at position " + startPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);
					continue;  //WILL NOT ADD THIS ANNOTATION
				}

				is.setPositions(Arrays.asList(new Pair<Integer, Integer>(isos.getSecond(), isos.getSecond())));
				annotation.setVariant(variantAnnotations.get(0).getVariant());
			}

			annotation.setTargetIsoformsMap(Arrays.asList(is));
			finalAnnotations.add(annotation);
		}

		resultEntry.setAnnotations(finalAnnotations);
		return resultEntry;

	}

}
