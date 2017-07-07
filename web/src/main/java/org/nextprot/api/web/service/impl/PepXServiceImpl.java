package org.nextprot.api.web.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.PeptideUtils;
import org.nextprot.api.web.domain.PepXResponse;
import org.nextprot.api.web.domain.PepXResponse.PepXEntryMatch;
import org.nextprot.api.web.domain.PepXResponse.PepXIsoformMatch;
import org.nextprot.api.web.domain.PepxUtils;
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
	public List<Entry> findEntriesWithPeptides(String peptides, boolean modeIsoleucine) {

		List<Entry> entries = new ArrayList<>();

		PepXResponse pepXResponse = getPepXResponse(peptides, modeIsoleucine);

		Set<String> entriesNames = pepXResponse.getEntriesNames();
		for (String entryName : entriesNames) {
			EntryConfig targetIsoconf = EntryConfig.newConfig(entryName).withTargetIsoforms().with("variant").withOverview().withoutAdditionalReferences().withoutProperties(); // .with("variant")
			Entry entry = entryBuilderService.build(targetIsoconf);

			List<Annotation> virtualAnnotations = new ArrayList<>();
			Set<String> peptidesForEntry = pepXResponse.getPeptidesForEntry(entryName);
			for(String peptide : peptidesForEntry){
				PepXEntryMatch pepxEntryMatch = pepXResponse.getPeptideMatch(peptide).getPepxMatchesForEntry(entryName);
				if(pepxEntryMatch != null && pepxEntryMatch.getIsoforms() != null && pepxEntryMatch.getIsoforms().size() > 0){
					virtualAnnotations.addAll(buildEntryWithVirtualAnnotations(peptide, modeIsoleucine, pepxEntryMatch.getIsoforms(), entry.getAnnotations(), entry.getIsoforms()));
				}
			}

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

	private PepXResponse getPepXResponse(String peptides, boolean modeIsoleucine) {
		
		String httpRequest = pepXUrl + "?format=json" + (modeIsoleucine ? ("&mode=IL&pep=" + peptides) : ("&pep=" + peptides));

		try {

			URL pepXUrl = new URL(httpRequest);
			URLConnection px = pepXUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(px.getInputStream()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			
			return PepxUtils.parsePepxResponse(sb.toString());
				
		
		} catch (IOException e) {
			throw new NextProtException(e);
		}

	}
	
	
	//This method is static friendly so that it can be tested ////////////////////////////////
	//CrossedCheckedWithEntryVariantsAndIsoforms
	static List<Annotation> buildEntryWithVirtualAnnotations(String peptide, boolean modeIsoleucine, List<PepXIsoformMatch> pepXisoforms, List<Annotation> varAnnotations, List<Isoform> isoforms) {

		
		List<Annotation> finalAnnotations = new ArrayList<>();
		for (PepXIsoformMatch isoNameAndOptionalPosition : pepXisoforms) {
			
			String isoformAc = isoNameAndOptionalPosition.getIsoformAccession();
			
			Annotation annotation = new Annotation();
			annotation.setAnnotationCategory(AnnotationCategory.PEPX_VIRTUAL_ANNOTATION);
			annotation.setCvTermName(peptide);
			annotation.setDescription("This virtual annotation describes the peptide " + peptide + " found in " + isoformAc);

			AnnotationIsoformSpecificity is = new AnnotationIsoformSpecificity();
			is.setIsoformAccession(isoformAc);
			if (isoNameAndOptionalPosition.getPosition() != null) {// It means there is a variant!!!

				int startPeptidePosition = isoNameAndOptionalPosition.getPosition();
				int endPeptidePosition = startPeptidePosition + peptide.length();
				List<Annotation> variantAnnotations = AnnotationUtils.filterAnnotationsBetweenPositions(startPeptidePosition, endPeptidePosition, varAnnotations, isoformAc);

				Isoform iso = IsoformUtils.getIsoformByIsoName(isoforms, isoformAc);
				if(iso == null){
					throw new NextProtException("The variant at " + startPeptidePosition +  " is not specific for this isoform " + isoformAc);
				}
				
				List<Annotation> validAnnotations = filterValidVariantAnnotations(peptide, modeIsoleucine, variantAnnotations, isoformAc, iso.getSequence());

				if ((validAnnotations == null) || validAnnotations.isEmpty()) {
					
					LOGGER.warn("No valid variants found for isoform " + isoformAc + " at position " + startPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);
					continue;
					
					//We used to throw an exception, but now we just skip
					//throw new NextProtException("No valid variants found for isoform " + isoformName + " at position" + startPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);
				
				}
				
				if (validAnnotations.size() > 1) {

					LOGGER.warn("There is more than 1 valid variant (" + validAnnotations.size() + ") for isoform (returning the 1st) " + isoformAc + " between position " + startPeptidePosition + " and " + endPeptidePosition + " for peptide " + peptide + " in mode IL:" + modeIsoleucine);

					//Takes only the first valid
					int startPos = validAnnotations.get(0).getStartPositionForIsoform(isoformAc);
					int endPos = validAnnotations.get(0).getEndPositionForIsoform(isoformAc);

					is.setFirstPosition(startPos);
					is.setLastPosition(endPos);

					AnnotationVariant var = validAnnotations.get(0).getVariant();
					annotation.setVariant(var);

					
				}else { //one variant on that position

					int startPos = validAnnotations.get(0).getStartPositionForIsoform(isoformAc);
					int endPos = validAnnotations.get(0).getEndPositionForIsoform(isoformAc);

					is.setFirstPosition(startPos);
					is.setLastPosition(endPos);

					AnnotationVariant var = validAnnotations.get(0).getVariant();
					annotation.setVariant(var);

				}

			}else { //No variant
				
				Isoform iso = IsoformUtils.getIsoformByIsoName(isoforms, isoformAc);
				String sequence = (iso != null) ? iso.getSequence() : null;
				
				boolean isPeptideContained = PeptideUtils.isPeptideContainedInTheSequence(peptide, sequence, modeIsoleucine);
				
				if(!isPeptideContained){
					LOGGER.warn("PepX returned a peptide (" + peptide + ") for an isoform (" + isoformAc + ") that is not in the current isoform in neXtProt");
					continue;
				}
				
				//We used to throw an exception, but this would break the program (the algorithm could be improved to detect the specific case where pepx return a peptide of length 6 and generate a real error on other cases)
				//NPreconditions.checkTrue(isPeptideContained, "PepX returned a peptide (" + peptide + ") for an isoform (" + isoformName + ") that is not in the current isoform in neXtProt");
				
			}
			
			annotation.addTargetingIsoforms(Arrays.asList(is));
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
					// pepx doesn't handle variant of type insert
					if(varAnnot.getVariant().getOriginal().length()==0){
						continue;
					}
					if(originalAA != varAnnot.getVariant().getOriginal().charAt(0)){
						LOGGER.warn("The original amino acid " + originalAA + " of the variant is not equal to the amino acid on the sequence of " + isoformName + " at position " + variantPosition );
						continue;
					}
					String variantAA = varAnnot.getVariant().getVariant();
					if (varAnnot.getVariant().getOriginal().length()==1) { // pepx only handles single substitution or deletion
						if (variantAA.length()==1) { // substitution 1 aa
							sequenceWithVariant.setCharAt(variantPosition, varAnnot.getVariant().getVariant().charAt(0));
							if(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequenceWithVariant.toString(), modeIsoLeucine)){//Check if the peptide is present with the sequence with the variant
								resultAnnotations.add(varAnnot);
							}
						} else if (variantAA.length()==0) { // deletion of 1 aa
							sequenceWithVariant.deleteCharAt(variantPosition);
							if(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequenceWithVariant.toString(), modeIsoLeucine)){//Check if the peptide is present with the sequence with the variant
								resultAnnotations.add(varAnnot);
							}
						}
					}
				}
			}
		}
		return resultAnnotations;
	}
	
	
	/*

	We started to filter out the results because 
	
	3 peptides showing error with pepX :
	- IHTGEKP
	- PYKCEECGK
	- RIHTGEKPYK
	
	ex erreur : http://dev-api.nextprot.org/entries/search/peptide?peptide=IHTGEKP&modeIL=true&clientInfo=nextprotTeam&applicationName=PeptideViewer
	
	Mis a part l'erreur qu'on a vu hier comme quoi ce premier peptide n'existait pas dans l'entrée Q96MM3,
	je tiens à noter que ces 3 peptides donnés en exemple sont trouvés à plusieurs positions dans l'isoform d'origine : NX_Q05481
	Mais je sais pas si ca joue vraiment.
	
	http://localhost:9000/app/?nxentry=NX_P46976&env=dev
	
	3 peptides working with pepX : 
	- TLTTNDAYAK
	- LVVLATPQVSDSMR
	- GALVLGSSL
	 */
}
