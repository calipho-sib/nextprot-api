package org.nextprot.api.web.ui.page;

import java.util.ArrayList;
import java.util.Arrays;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

public class IdentifiersPageConfig extends SimplePageConfig {

	private static final IdentifiersPageConfig INSTANCE = new IdentifiersPageConfig();

	public static IdentifiersPageConfig getInstance() { return INSTANCE; }
	
	private IdentifiersPageConfig() {
		
		annotations = new ArrayList<>();

		features = new ArrayList<>();
		
		xrefs = Arrays.asList("CCDS", "GeneCards", "GeneID",  "HGNC", "H-InvDB", "HPA", "HPRD","KEGG","LOC", 
			    "MIM",  "NextBio", "PDB", "PharmGKB", "PIR","RefSeq", "UCSC","UniGene",
                "ChEMBL");
		
	}
	
	@Override
	public boolean filterOutAnnotation(Annotation a) {
		return true;
	}

	@Override
	public boolean filterOutXref(DbXref x) {
		return true;
	}

}
