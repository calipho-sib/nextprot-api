package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.ui.page.EntryPage;
import org.nextprot.api.web.ui.page.PageDisplayRequirement;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

/**
 * A base class for page display requirement.
 *
 * A page requirement needs to test data from {@code Entry} with criteria based on:
 * <ul>
 * <li>annotation categories</li>
 * <li>feature categories</li>
 * <li>cross references</li>
 * </ul>
 *
 *
 *
 * Subclasses should give get white lists implementation
 */
public abstract class BasePageDisplayRequirement implements PageDisplayRequirement {

	private final EntryPage entryPage;

	BasePageDisplayRequirement(EntryPage entryPage) {

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
	protected abstract @Nonnull List<AnnotationCategory> getAnnotationCategoryWhiteList();

	/**
	 * @return a non null white list of feature category
	 */
	protected abstract @Nonnull List<AnnotationCategory> getFeatureCategoryWhiteList();

	/**
	 * @return a non null white list of xref database name
	 */
	protected abstract @Nonnull List<String> getXrefDbNameWhiteList();

	/**
	 * This class contains all implementations of BasePageDisplayRequirement
	 */
	public static class AllPageDisplayRequirements {

		private static final AllPageDisplayRequirements INSTANCE = new AllPageDisplayRequirements();

		private final Set<PageDisplayRequirement> requirements;

		private AllPageDisplayRequirements() {

			requirements = new HashSet<>();
			requirements.add(new ExonsPageDisplayRequirement());
			requirements.add(new ExpressionPageDisplayRequirement());
			requirements.add(new FunctionPageDisplayRequirement());
			requirements.add(new GeneIdentifiersPageDisplayRequirement());
			requirements.add(new IdentifiersPageDisplayRequirement());
			requirements.add(new InteractionsPageDisplayRequirement());
			requirements.add(new LocalisationPageDisplayRequirement());
			requirements.add(new MedicalPageDisplayRequirement());
			requirements.add(new PeptidesPageDisplayRequirement());
			requirements.add(new PhenotypesPageDisplayRequirement());
			requirements.add(new ProteomicsPageDisplayRequirement());
			requirements.add(new SequencePageDisplayRequirement());
			requirements.add(new StructuresPageDisplayRequirement());
		}

		public static AllPageDisplayRequirements getInstance() {
			return INSTANCE;
		}

		public Stream<PageDisplayRequirement> getPageRequirements() {

			return requirements.stream();
		}
	}
}
