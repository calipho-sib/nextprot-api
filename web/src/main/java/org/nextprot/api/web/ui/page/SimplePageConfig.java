package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class SimplePageConfig {

	private final String pageName;
	private final List<AnnotationCategory> mandatoryAnnotationCategoryList;
	private final List<AnnotationCategory> mandatoryFeatureList;
	private final List<String> mandatoryXrefDbNameList;

	SimplePageConfig(@Nonnull String pageName) {

		Objects.requireNonNull(getSelectedAnnotationCategoryList(), "selected annotation category list should not be null");
		Objects.requireNonNull(getSelectedXrefDbNameList(), "selected xref db name list should not be null");
		Objects.requireNonNull(getSelectedFeatureList(), "selected feature list should not be null");

		this.pageName = pageName;
		mandatoryAnnotationCategoryList = getSelectedAnnotationCategoryList().stream().filter(a -> !filterOutAnnotationCategoryPredicate().test(a)).collect(Collectors.toList());
		mandatoryXrefDbNameList = getSelectedXrefDbNameList().stream().filter(a -> !filterOutXrefDbNamePredicate().test(a)).collect(Collectors.toList());
		mandatoryFeatureList = getSelectedFeatureList();
	}

	/**
	 * Check entry content for current page
	 * @param entry the entry to check content
	 * @return true if has content required by the page
	 */
	public boolean hasContent(@Nonnull Entry entry) {

		// test xrefs
		if (entry.getXrefs().stream().anyMatch(xr -> mandatoryXrefDbNameList.contains(xr.getDatabaseName())))
			return true;

		// then annotations
		if (entry.getAnnotations().stream().anyMatch(a -> mandatoryAnnotationCategoryList.contains(a.getAPICategory())))
			return true;

		// then features
		return entry.getAnnotations().stream()
				.anyMatch(a -> mandatoryFeatureList.contains(a.getAPICategory()));
	}

	/**
	 * @return page name
	 */
	public String getPageName() {

		return pageName;
	}

	/**
	 * @return a non null list of selected annotation category
	 */
	protected abstract @Nonnull List<AnnotationCategory> getSelectedAnnotationCategoryList();

	/**
	 * @return a non null list of selected annotation category
	 */
	protected abstract @Nonnull List<AnnotationCategory> getSelectedFeatureList();

	/**
	 * @return a non null list of selected annotation category
	 */
	protected abstract @Nonnull List<String> getSelectedXrefDbNameList();

	/**
	 * @return a predicate that return true if annotation category have to be unselected
	 * keep all by default ?
	 */
	protected @Nonnull Predicate<AnnotationCategory> filterOutAnnotationCategoryPredicate() {
		return ac -> false;
	}

	/**
	 * @return a predicate that return true if xref db name have to be unselected
	 * keep all by default ?
	 */
	protected @Nonnull Predicate<String> filterOutXrefDbNamePredicate() {
		return xref -> false;
	}
}
