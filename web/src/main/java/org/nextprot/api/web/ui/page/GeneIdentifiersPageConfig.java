package org.nextprot.api.web.ui.page;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneIdentifiersPageConfig extends SimplePageConfig {

	private static final GeneIdentifiersPageConfig INSTANCE = new GeneIdentifiersPageConfig();

	public static GeneIdentifiersPageConfig getInstance() { return INSTANCE; }
	
	private GeneIdentifiersPageConfig() {
		
		annotations = new ArrayList<>();

		features = new ArrayList<>();
		
		xrefs = Arrays.asList("CCDS","GeneCards","GeneID","HGNC", "H-InvDB","KEGG", "LOC","MIM","RefSeq", "UniGene","UCSC");
		
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
