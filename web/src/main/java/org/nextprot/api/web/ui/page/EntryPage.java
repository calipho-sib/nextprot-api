package org.nextprot.api.web.ui.page;

import org.nextprot.api.web.ui.page.impl.*;

import java.util.function.Supplier;

public enum EntryPage {

	FUNCTION("Function", FunctionPageView::new, ""),
	MEDICAL("Medical", MedicalPageView::new),
	EXPRESSION("Expression", ExpressionPageView::new),
	INTERACTIONS("Interactions", InteractionsPageView::new),
	LOCALIZATION("Localization", LocalisationPageView::new),
	SEQUENCE("Sequence", SequencePageView::new),
	PROTEOMICS("Proteomics", ProteomicsPageView::new),
	STRUCTURES("Structures", StructuresPageView::new),
	PROTEIN_IDENTIFIERS("Identifiers", IdentifiersPageView::new),
	PEPTIDES("Peptides", PeptidesPageView::new),
	PHENOTYPES("Phenotypes", PhenotypesPageView::new),
	EXONS("Exons", ExonsPageView::new),
	GENE_IDENTIFIERS("Gene Identifiers", GeneIdentifiersPageView::new, "gene_identifiers")
	;

	private final String link;
	private final Supplier<PageView> pageViewBuilder;
	private final String label;

	EntryPage(String label, Supplier<PageView> pageView) {

		this(label, pageView, label.toLowerCase());
	}

	EntryPage(String label, Supplier<PageView> pageView, String link) {

		this.label = label;
		this.pageViewBuilder = pageView;
		this.link = link;
	}

	public String getLabel() {
		return label;
	}

	public String getLink() {
		return link;
	}

	public PageView buildPageView() {
		return pageViewBuilder.get();
	}
}
