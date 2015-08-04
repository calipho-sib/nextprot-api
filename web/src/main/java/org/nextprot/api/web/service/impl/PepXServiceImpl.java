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
			EntryConfig targetIsoconf = EntryConfig.newConfig(entryName).withTargetIsoforms().withoutAdditionalReferences().withoutProperties(); //.with("variant")
			Entry entry = entryBuilderService.build(targetIsoconf);
			entries.add(filterVariantAnnotsWithPepxResults(peptide, modeIsoleucine, entry, entriesMap.get(entryName)));
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

	// Response is something like [O60678,1,297] or [O60678,1]
	// In the first case it means isoform 1
	private Entry filterVariantAnnotsWithPepxResults(String peptide, boolean modeIsoleucine, Entry entry, List<Pair<Integer, Integer>> isoformNamesAndOptionalPosition) {

		List<Annotation> finalAnnotations = new ArrayList<>();
		for (Pair<Integer, Integer> isos : isoformNamesAndOptionalPosition) {
			String isoformName = entry.getUniqueName() + "-" + isos.getFirst();
			
			/*if(isos.getSecond() != null){
				int startPeptidePosition = isos.getSecond();
				int endPeptidePosition = startPeptidePosition + peptide.length(); 
				LOGGER.warn("Variant annotations count " + entry.getAnnotations());
				for (Annotation variantAnnotation : entry.getAnnotations()) {
					if(variantAnnotation.isSpecificForIsoform(isoName)){

						int variantPosition = variantAnnotation.getStartPositionForIsoform(isoName); // start and end should be the same
						if((variantPosition >= startPeptidePosition) && (variantPosition <= endPeptidePosition)){ //in between

							//check that all isoforms are present
							//variantAnnotation.getTargetingIsoformsMap();
							
							finalAnnotations.add(variantAnnotation);
							break;
						}

					}else {
						LOGGER.warn("PepX returned a variant that we do not consider a cosmic variant for isoform " + isoName + " at position" + startPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);
					}
				}
			}else {*/
				
				Annotation annotation = new Annotation();
				annotation.setCategoryOnly("pepx-virtual-annotation");
				annotation.setCvTermName(peptide);
				annotation.setDescription("This virtual annotation describes the peptide " + peptide + " found in " + isoformName);
				
				IsoformSpecificity is = new IsoformSpecificity(null, isoformName);
				if(isos.getSecond() != null){
					is.setPositions(Arrays.asList(new Pair<Integer, Integer>(isos.getSecond(), isos.getSecond())));
				}
				List<IsoformSpecificity> targetingIsoforms = new ArrayList<>();
				targetingIsoforms.add(is); // what's the isoform name ???
				
				annotation.setTargetIsoformsMap(targetingIsoforms);
				finalAnnotations.add(annotation);
				//TODO create a virtual annotation
			//}
		}
		
		entry.setAnnotations(finalAnnotations);
		return entry;

	}

}
