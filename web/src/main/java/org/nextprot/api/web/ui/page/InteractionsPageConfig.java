package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Arrays;

public class InteractionsPageConfig extends SimplePageConfig {

	private static final InteractionsPageConfig INSTANCE = new InteractionsPageConfig();

	public static InteractionsPageConfig getInstance() { return INSTANCE; }
	
	private InteractionsPageConfig() {
		
		annotations = Arrays.asList(
	               AnnotationCategory.INTERACTION_INFO, // = NP1 SUBUNIT,
	               AnnotationCategory.MISCELLANEOUS
		);
		
		features = Arrays.asList(AnnotationCategory.INTERACTING_REGION);
		
		xrefs = Arrays.asList(
				"BindingDB","DIP","IntAct","MINT","STRING",
	               "SignaLink", "BioGrid","SIGNOR");

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
