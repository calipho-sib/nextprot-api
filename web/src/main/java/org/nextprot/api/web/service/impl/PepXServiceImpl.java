package org.nextprot.api.web.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.PeptideUtils;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

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

		List<Entry> entries = new ArrayList<>();

		Map<String, List<Pair<String, Integer>>> entriesMap = getPepXResponse(peptide, modeIsoleucine);

		Set<String> entriesNames = entriesMap.keySet();
		for (String entryName : entriesNames) {
			EntryConfig targetIsoconf = EntryConfig.newConfig(entryName).withTargetIsoforms().with("variant").withOverview().withoutAdditionalReferences().withoutProperties(); // .with("variant")
			Entry entry = entryBuilderService.build(targetIsoconf);
			List<Annotation> virtualAnnotations = buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, entriesMap.get(entryName), entry.getAnnotations(), entry.getIsoforms());

			if((virtualAnnotations != null) && (!virtualAnnotations.isEmpty())){

				Entry resultEntry = new Entry(entry.getUniqueName());
				//Adds the overview as well
				resultEntry.setOverview(entry.getOverview());
				resultEntry.setAnnotations(virtualAnnotations);

				entries.add(resultEntry);
			}
		}

		return entries;

	}

	private Map<String, List<Pair<String, Integer>>> getPepXResponse(String peptide, boolean modeIsoleucine) {

		Map<String, List<Pair<String, Integer>>> entriesMap = new HashMap<>();
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
			for (int i = 1; i < acs.length; i++) { // Do not take 1st row
				if(acs[i].contains("match")) break; // sometimes 
				
				String[] ei = acs[i].split("-");
				String currentEntry = "NX_" + ei[0];
				int isoNumber = Integer.valueOf(ei[1]);
				if (ei.length == 2 || ei.length == 3) {
					if (!entriesMap.containsKey(currentEntry)) {
						entriesMap.put(currentEntry, new ArrayList<Pair<String, Integer>>());
					}
					Integer position = (ei.length == 3) ? (Integer.valueOf(ei[2])) : null;
					entriesMap.get(currentEntry).add(new Pair<>(currentEntry + "-" + isoNumber, position));
				} else {
					throw new NextProtException("Unexpected format from pepX on row " + i + ": " + acs[i]);
				}
			}

			in.close();

		} catch (IOException e) {
			throw new NextProtException(e);
		}

		return entriesMap;
	}
	
	
	//This method is static friendly so that it can be tested ////////////////////////////////
	//CrossedCheckedWithEntryVariantsAndIsoforms
	static List<Annotation> buildEntryWithVirtualAnnotations(String peptide, boolean modeIsoleucine, List<Pair<String, Integer>> isoformNamesAndOptionalPosition, List<Annotation> varAnnotations, List<Isoform> isoforms) {

		
		List<Annotation> finalAnnotations = new ArrayList<>();
		for (Pair<String, Integer> isoNameAndOptionalPosition : isoformNamesAndOptionalPosition) {
			
			String isoformName = isoNameAndOptionalPosition.getFirst();
			
			Annotation annotation = new Annotation();
			annotation.setCategoryOnly("pepx-virtual-annotation");
			annotation.setCvTermName(peptide);
			annotation.setDescription("This virtual annotation describes the peptide " + peptide + " found in " + isoformName);

			AnnotationIsoformSpecificity is = new AnnotationIsoformSpecificity();
			is.setIsoformName(isoformName);
			if (isoNameAndOptionalPosition.getSecond() != null) {// It means there is a variant!!!

				int startPeptidePosition = isoNameAndOptionalPosition.getSecond();
				int endPeptidePosition = startPeptidePosition + peptide.length();
				List<Annotation> variantAnnotations = AnnotationUtils.filterAnnotationsBetweenPositions(startPeptidePosition, endPeptidePosition, varAnnotations, isoformName);

				Isoform iso = IsoformUtils.getIsoformByIsoName(isoforms, isoformName);
				if(iso == null){
					throw new NextProtException("The variant at " + startPeptidePosition +  " is not specific for this isoform " + isoformName);
				}
				
				List<Annotation> validAnnotations = filterValidVariantAnnotations(peptide, modeIsoleucine, variantAnnotations, isoformName, iso.getSequence());

				if ((validAnnotations == null) || validAnnotations.isEmpty()) {
					
					throw new NextProtException("No valid variants found for isoform " + isoformName + " at position" + startPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);
				
				} else if (validAnnotations.size() > 1) {

					LOGGER.warn("There is more than 1 valid variant (" + validAnnotations.size() + ") for isoform (returning the 1st) " + isoformName + " between position " + startPeptidePosition + " and " + endPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);

					//Takes only the first valid
					int startPos = validAnnotations.get(0).getStartPositionForIsoform(isoformName);
					int endPos = validAnnotations.get(0).getEndPositionForIsoform(isoformName);

					is.setFirstPosition(startPos);
					is.setLastPosition(endPos);

					AnnotationVariant var = validAnnotations.get(0).getVariant();
					annotation.setVariant(var);

					
				}else { //one variant on that position

					int startPos = validAnnotations.get(0).getStartPositionForIsoform(isoformName);
					int endPos = validAnnotations.get(0).getEndPositionForIsoform(isoformName);

					is.setFirstPosition(startPos);
					is.setLastPosition(endPos);

					AnnotationVariant var = validAnnotations.get(0).getVariant();
					annotation.setVariant(var);

				}

			}else { //No variant
				
				Isoform iso = IsoformUtils.getIsoformByIsoName(isoforms, isoformName);
				String sequence = (iso != null) ? iso.getSequence() : null;
				NPreconditions.checkTrue(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequence, modeIsoleucine), "PepX returned a peptide (" + peptide + ") for an isoform (" + isoformName + ") that is not in the current isoform in neXtProt");
				
			}
			
			annotation.setTargetingIsoforms(Arrays.asList(is));
			finalAnnotations.add(annotation);
		}

		return finalAnnotations;

	}

	//This method is static friendly so that it can be tested ////////////////////////////////
	static List<Annotation> filterValidVariantAnnotations(String peptide, boolean modeIsoLeucine, List<Annotation> variantAnnotations, String isoformName, String originalSequence) {
		List<Annotation> resultAnnotations = new ArrayList<>();
		for(Annotation varAnnot : variantAnnotations){
			if(varAnnot.isAnnotationPositionalForIsoform(isoformName)){ //Check that the isoform is valid
				//In this case the peptide is the sequence and the variant is the peptide
				if(PeptideUtils.isPeptideContainedInTheSequence(varAnnot.getVariant().getVariant(), peptide, modeIsoLeucine)){//Check if the variant is present in the peptide
					StringBuilder sequenceWithVariant = new StringBuilder(originalSequence);
					int variantPosition = varAnnot.getStartPositionForIsoform(isoformName) - 1;
					char originalAA = originalSequence.charAt(variantPosition);
					if(originalAA != varAnnot.getVariant().getOriginal().charAt(0)){
						throw new NextProtException("The amino acid " + originalAA + " is not present on the sequence of the isoform (position) " + "(" + isoformName + ")" + variantPosition );
					}
					sequenceWithVariant.setCharAt(variantPosition, varAnnot.getVariant().getVariant().charAt(0));
					if(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequenceWithVariant.toString(), modeIsoLeucine)){//Check if the peptide is present with the sequence with the variant
						resultAnnotations.add(varAnnot);
					}
				}
			}

		}
		return resultAnnotations;
	}

}
