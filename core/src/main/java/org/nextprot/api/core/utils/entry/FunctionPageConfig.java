package org.nextprot.api.core.utils.entry;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionPageConfig extends SimplePageConfig {

	private static final FunctionPageConfig INSTANCE = new FunctionPageConfig();

	public static FunctionPageConfig getInstance() { return INSTANCE; }
	
	private FunctionPageConfig() {
		
		annotations = Arrays.asList(
	               AnnotationCategory.ALLERGEN,
		           AnnotationCategory.FUNCTION_INFO, 
			       AnnotationCategory.GO_MOLECULAR_FUNCTION,
			       AnnotationCategory.GO_BIOLOGICAL_PROCESS,
			       AnnotationCategory.ENZYME_REGULATION, 
				   AnnotationCategory.CATALYTIC_ACTIVITY,
			       AnnotationCategory.COFACTOR,
			       AnnotationCategory.PATHWAY, 
			       //AnnotationCategory.DISRUPTIVE_PHENOTYPE, no data in NP1
			       AnnotationCategory.CAUTION,
			       AnnotationCategory.MISCELLANEOUS		
		);
		
		features = new ArrayList<>();
				
		xrefs = Arrays.asList("BRENDA", "CAZy", "KEGGPathway", "MEROPS", "PeroxiBase",
		           "BioCyc", "Reactome","Pathway_Interaction_DB", "REBASE", "TCDB",
	               "GeneWiki", "SABIO-RK", "GenomeRNAi", "GuidetoPHARMACOLOGY", "PRO","MoonProt","ESTHER", "SwissLipids");

		
	}
	
	@Override
	public boolean filterOutAnnotation(Annotation a) {
		
		String termAc = a.getCvTermAccessionCode();
		if (termAc!=null && "GO:0003674 GO:0008150".contains(termAc)) {
			return false;
		} else if (a.getAPICategory().equals(AnnotationCategory.CAUTION)) {
			
		} else if (a.getAPICategory().equals(AnnotationCategory.MISCELLANEOUS)) {
			
		}
		
		return true;
	}

	@Override
	public boolean filterOutXref(DbXref x) {
		return true;
	}

}
