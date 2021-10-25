package org.nextprot.api.core.utils;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

import java.util.ArrayList;
import java.util.List;

public class QuickAndDirtyKeywordProcessor {

	public static void processKeywordAnnotations(List<Annotation> annotations, String entryName, List<Isoform> isoforms) {
		
		// embryo of what should be generalized for all keywords
		
		// Important Note: 
		// the keyword can be inferred from the term itself (see external link of PTM terms, see also np1 implementation)
		// below everything is hardcoded as tmp quick & dirty solution
		
		boolean shouldHaveKW_0325 = false; // Glyco
		boolean shouldHaveKW_0488 = false; // Methyl
		boolean shouldHaveKW_0597 = false; // Phospho
		boolean shouldHaveKW_2001 = false; // Rare disease (new :-)
		boolean hasKW_0325 = false;
		boolean hasKW_0488 = false;
		boolean hasKW_0597 = false;
		boolean hasKW_2001 = false;
		
		for (Annotation a:annotations) {
						
			if (a.getAPICategory() == AnnotationCategory.GLYCOSYLATION_SITE) {
				if      ("PTM-0528".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0529".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0530".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0531".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0532".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0551".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0565".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0568".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;	
				else if ("PTM-0553".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0574".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0552".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0550".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0505".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0582".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
				else if ("PTM-0580".equals(a.getCvTermAccessionCode())) shouldHaveKW_0325 = true;
			    
			} else if (a.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE) {				
				if      ("PTM-0237".equals(a.getCvTermAccessionCode())) shouldHaveKW_0488 = true;
				else if ("PTM-0253".equals(a.getCvTermAccessionCode())) shouldHaveKW_0597 = true;
				else if ("PTM-0254".equals(a.getCvTermAccessionCode())) shouldHaveKW_0597 = true;
				else if ("PTM-0255".equals(a.getCvTermAccessionCode())) shouldHaveKW_0597 = true;				

			} else if (a.getAPICategory() == AnnotationCategory.DISEASE) {	
				for (AnnotationEvidence evi: a.getEvidences()) {
					if (evi.getAssignedBy().equals("Orphanet") && ! a.getDescription().contains("NON RARE")) {
						shouldHaveKW_2001 = true;
					}
				}
				
			} else if (a.getAPICategory() == AnnotationCategory.UNIPROT_KEYWORD) {
				if      ("KW-0325".equals(a.getCvTermAccessionCode())) hasKW_0325 = true;				
				else if ("KW-0488".equals(a.getCvTermAccessionCode())) hasKW_0488 = true;				
				else if ("KW-0597".equals(a.getCvTermAccessionCode())) hasKW_0597 = true;								
			}
		}
		// at the moment we only add missing keywords for existing PTMs imported from nxflat on 9 Sept 2018
		if (shouldHaveKW_0325) {
			if (! hasKW_0325) {
				annotations.add(createKeywordAnnotation("KW-0325", "Glycoprotein", "PTM", entryName,  isoforms));
			} else {
				//System.out.println("KEYWORD annotation already exists for " + entryName + " : KW-0325 - Glycoprotein");
			}
		}
		if (shouldHaveKW_0488) {
			if (! hasKW_0488) {
				annotations.add(createKeywordAnnotation("KW-0488", "Methylation", "PTM", entryName, isoforms));
			} else {
				//System.out.println("KEYWORD annotation already exists for " + entryName + " : KW-0488 - Methylation");				
			}
		}
		if (shouldHaveKW_0597) {
			if (! hasKW_0597) {
				annotations.add(createKeywordAnnotation("KW-0597", "Phosphoprotein", "PTM", entryName, isoforms));
			} else {
				//System.out.println("KEYWORD annotation already exists for " + entryName + " : KW-0597 - Phosphoprotein");				
			}
		}
		if (shouldHaveKW_2001) {
			if (! hasKW_2001) {
				annotations.add(createKeywordAnnotation("KW-2001", "Rare disease", "Disease", entryName, isoforms));
			} else {
				//System.out.println("KEYWORD annotation already exists for " + entryName + " : KW-0597 - Phosphoprotein");				
			}
		}
		
	}
	
	public static Annotation createKeywordAnnotation(String kwAccession, String kwName, String kwCategory, String entryName, List<Isoform> isoforms) {

		long annotId = IdentifierOffset.KEYWORD_ANNOTATION_ID_COUNTER.incrementAndGet();
		Annotation annot = new Annotation();
		annot.setAnnotationId(annotId);
		annot.setCategory(AnnotationCategory.UNIPROT_KEYWORD.getDbAnnotationTypeName());
		annot.setCvTermAccessionCode(kwAccession);
		annot.setCvTermName(kwName);
		annot.setCvApiName("UniprotKeywordCv");
		annot.setCvTermType(kwCategory);      // should be read from property of term category
		annot.setCvTermDescription(kwName); // should be read from property of term 
		annot.setDescription(kwName);
		annot.setParentXref(null);
		annot.setQualityQualifier("GOLD");  // like in NP1 processor
		annot.setSynonym(null);
		annot.setUniqueName("AN_"+ entryName.substring(3) + "_KW_" + annotId);
		annot.setVariant(null);		

		System.out.println("Creating NEW KEYWORD annotation " + annot.getUniqueName() + " with keyword " + kwAccession + ":" + kwName);
		
		// - - - - - - - - - - - - - - - - - - - - 
		// empty list of annotation evidences
		// - - - - - - - - - - - - - - - - - - - - 
		List<AnnotationEvidence> evidences = new ArrayList<>();
		annot.setEvidences(evidences);
		
		// - - - - - - - - - - - - - - - - - - - - 
		// empty list of annotation properties
		// - - - - - - - - - - - - - - - - - - - - 
		List<AnnotationProperty> props = new ArrayList<>();
		annot.addProperties(props);
		// - - - - - - - - - - - - - - - - - - - - 
		// annotation isoform specificity
		// - - - - - - - - - - - - - - - - - - - - 
		List<AnnotationIsoformSpecificity> isospecs = new ArrayList<>();
		for (Isoform iso: isoforms) {
			AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();
			spec.setAnnotationId(annotId);
			spec.setIsoformAccession(iso.getIsoformAccession());
			spec.setSpecificity("UNKNOWN");
			isospecs.add(spec);
		}
		annot.addTargetingIsoforms(isospecs);
		return annot;
	}
	
	
}
