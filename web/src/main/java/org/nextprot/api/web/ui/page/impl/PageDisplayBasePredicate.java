package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.ui.page.EntryPage;
import org.nextprot.api.web.ui.page.PageDisplayPredicate;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents an entry predicate (boolean-valued function) to test displayability of a specific page.
 *
 * Predicate should be based on the following entry data:
 * <ul>
 * <li>annotation categories</li>
 * <li>feature categories</li>
 * <li>cross references</li>
 * </ul>
 */
public abstract class PageDisplayBasePredicate implements PageDisplayPredicate {

	private final EntryPage entryPage;

	PageDisplayBasePredicate(EntryPage entryPage) {

		Objects.requireNonNull(entryPage, "page should have a defined name");
		Objects.requireNonNull(getAnnotationCategoryWhiteList(), "selected annotation category list should not be null");
		Objects.requireNonNull(getXrefDbNameWhiteList(), "selected xref db name list should not be null");
		Objects.requireNonNull(getFeatureCategoryWhiteList(), "selected feature list should not be null");

		this.entryPage = entryPage;
	}

	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * @param entry the entry to check content
	 * @return true if page should be display
	 */
	@Override
	public boolean doDisplayPage(@Nonnull Entry entry) {

		// test xrefs
		if (entry.getXrefs().stream()
				.filter(xref -> !filterOutXrefDbName(xref))
				.anyMatch(xr -> getXrefDbNameWhiteList().contains(xr.getDatabaseName())))
			return true;

		// then annotations
		if (entry.getAnnotations().stream()
				.map(Annotation::getAPICategory)
				.filter(ac -> !filterOutAnnotationCategory(ac))
				.anyMatch(getAnnotationCategoryWhiteList()::contains))
			return true;

		// then features
		return entry.getAnnotations().stream()
				.map(Annotation::getAPICategory)
				.filter(ac -> !filterOutFeatureCategory(ac))
				.anyMatch(getFeatureCategoryWhiteList()::contains);
	}

	/**
	 * @return page
	 */
	@Override
	public EntryPage getPage() {

		return entryPage;
	}
	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * Filter entry annotations based on category criteria
	 * @param annotationCategory annotation category to test
	 * @return true if annotation category passes the filter
	 */
	protected boolean filterOutAnnotationCategory(AnnotationCategory annotationCategory) {
		return false;
	}

	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * Filter entry features based on category criteria
	 * @param featureCategory feature category to test
	 * @return true if feature category passes the filter
	 */
	protected boolean filterOutFeatureCategory(AnnotationCategory featureCategory) {
		return false;
	}

	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * Filter entry xrefs
	 * @param xref cross ref to test
	 * @return true if xref passes the filter
	 */
	protected boolean filterOutXrefDbName(DbXref xref) {
		return false;
	}

	/**
	 * @return a non null white list of annotation category
	 */
	@Nonnull protected abstract List<AnnotationCategory> getAnnotationCategoryWhiteList();

	/**
	 * @return a non null white list of feature category
	 */
	@Nonnull protected abstract List<AnnotationCategory> getFeatureCategoryWhiteList();

	/**
	 * @return a non null white list of xref database name
	 */
	@Nonnull protected abstract List<String> getXrefDbNameWhiteList();

	/**
	 * This class contains all different entry page predicates
	 */
	public static class Predicates {

		private static final Predicates INSTANCE = new Predicates();

		private final Set<PageDisplayPredicate> predicates;

		private Predicates() {

			predicates = new HashSet<>();
			predicates.add(new ExonsPageDisplayPredicate());
			predicates.add(new ExpressionPageDisplayPredicate());
			predicates.add(new FunctionPageDisplayPredicate());
			predicates.add(new GeneIdentifiersPageDisplayPredicate());
			predicates.add(new IdentifiersPageDisplayPredicate());
			predicates.add(new InteractionsPageDisplayPredicate());
			predicates.add(new LocalisationPageDisplayPredicate());
			predicates.add(new MedicalPageDisplayPredicate());
			predicates.add(new PeptidesPageDisplayPredicate());
			predicates.add(new PhenotypesPageDisplayPredicate());
			predicates.add(new ProteomicsPageDisplayPredicate());
			predicates.add(new SequencePageDisplayPredicate());
			predicates.add(new StructuresPageDisplayPredicate());
		}

		public static Predicates getInstance() {
			return INSTANCE;
		}

		public Stream<PageDisplayPredicate> getPagePredicates() {

			return predicates.stream();
		}
	}
}
