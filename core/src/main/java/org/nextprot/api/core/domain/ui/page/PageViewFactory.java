package org.nextprot.api.core.domain.ui.page;

import org.nextprot.api.core.domain.ui.page.impl.*;

import java.util.ArrayList;
import java.util.List;
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

	private final PageView pageView;
	private static final List<PageView> pageViews = new ArrayList<>();

	static {
        for (PageViewFactory page : PageViewFactory.values()) {

            pageViews.add(page.getPageView());
        }
    }

	PageViewFactory(Supplier<PageView> supplier) {

	    pageView = supplier.get();
	}

	public PageView getPageView() {
		return pageView;
	}

    public static List<PageView> getPageViews() {

        return pageViews;
    }
}
