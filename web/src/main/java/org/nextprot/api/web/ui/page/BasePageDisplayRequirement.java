package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.ui.PageDisplayRequirement;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

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

	private final String pageName;
	private final List<AnnotationCategory> annotationCategoryWhiteList;
	private final List<AnnotationCategory> featureCategoryWhiteList;
	private final List<String> xrefDbNameWhiteList;

	BasePageDisplayRequirement(String pageName) {

		Objects.requireNonNull(pageName, "page should have a defined name");
		Objects.requireNonNull(getAnnotationCategoryWhiteList(), "selected annotation category list should not be null");
		Objects.requireNonNull(getXrefDbNameWhiteList(), "selected xref db name list should not be null");
		Objects.requireNonNull(getFeatureCategoryWhiteList(), "selected feature list should not be null");

		this.pageName = pageName;
		annotationCategoryWhiteList = getAnnotationCategoryWhiteList();
		xrefDbNameWhiteList = getXrefDbNameWhiteList();
		featureCategoryWhiteList = getFeatureCategoryWhiteList();
	}

	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * @param entry the entry to check content
	 * @return true if page should be display
	 */
	public boolean doDisplayPage(@Nonnull Entry entry) {

		// test xrefs
		if (entry.getXrefs().stream()
				.filter(xref -> !filterOutXrefDbName(xref))
				.anyMatch(xr -> xrefDbNameWhiteList.contains(xr.getDatabaseName())))
			return true;

		// then annotations
		if (entry.getAnnotations().stream()
				.map(Annotation::getAPICategory)
				.filter(ac -> !filterOutAnnotationCategory(ac))
				.anyMatch(annotationCategoryWhiteList::contains))
			return true;

		// then features
		return entry.getAnnotations().stream()
				.map(Annotation::getAPICategory)
				.filter(ac -> !filterOutFeatureCategory(ac))
				.anyMatch(featureCategoryWhiteList::contains);
	}

	/**
	 * @return the page name
	 */
	public String getPageName() {

		return pageName;
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

}
