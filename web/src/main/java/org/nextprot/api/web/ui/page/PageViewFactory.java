package org.nextprot.api.web.ui.page;

import org.nextprot.api.web.ui.page.impl.*;

import java.util.function.Supplier;

public enum PageViewFactory {

	FUNCTION(FunctionPageView::new),
	MEDICAL(MedicalPageView::new),
	EXPRESSION(ExpressionPageView::new),
	INTERACTIONS(InteractionsPageView::new),
	LOCALIZATION(LocalisationPageView::new),
	SEQUENCE(SequencePageView::new),
	PROTEOMICS(ProteomicsPageView::new),
	STRUCTURES(StructuresPageView::new),
	PROTEIN_IDENTIFIERS(IdentifiersPageView::new),
	PEPTIDES(PeptidesPageView::new),
	PHENOTYPES(PhenotypesPageView::new),
	EXONS(ExonsPageView::new),
	GENE_IDENTIFIERS(GeneIdentifiersPageView::new)
	;

	private final Supplier<PageView> pageViewBuilder;

	PageViewFactory(Supplier<PageView> pageView) {
		this.pageViewBuilder = pageView;
	}

	public PageView build() {
		return pageViewBuilder.get();
	}
}
