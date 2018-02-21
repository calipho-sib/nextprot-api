package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.ui.page.PageView;
import org.nextprot.api.core.utils.XrefUtils;
import org.nextprot.api.core.utils.annot.AnnotationUtils;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an entry view model
 * 
 * 1/ used to test displayability (boolean-valued function) of a specific page.
 * Predicate should be based on the following entry data:
 * <ul>
 * <li>annotation categories</li>
 * <li>feature categories</li>
 * <li>cross references</li>
 * </ul>
 * 
 * 2/ used to get the list of xrefs that should be seen in the further external link section
 * The function based on the full content of an entry
 * 
 * 3/ used to get a list of general annotations suitable for the generic annotation viewer
 * 
 * 4/ used to get a list of positional annotations suitable for the triple viewer
 * 
 */
public abstract class PageViewBase implements PageView {

	PageViewBase() {

		Objects.requireNonNull(getAnnotationCategoryWhiteList(), "selected annotation category list should not be null");
		Objects.requireNonNull(getXrefDbNameWhiteList(), "selected xref db name list should not be null");
		Objects.requireNonNull(getFeatureCategoryWhiteList(), "selected feature list should not be null");
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
				.filter(xref -> !filterOutXref(xref))
				.anyMatch(xr -> getXrefDbNameWhiteList().contains(xr.getDatabaseName())))
			return true;

		// then annotations
		if (entry.getAnnotations().stream()
				.filter(a -> getAnnotationCategoryWhiteList().contains(a.getAPICategory()))
				.anyMatch(a -> ! filterOutAnnotation(a)))
			return true;

		// then features
		return entry.getAnnotations().stream()
				.filter(a -> getFeatureCategoryWhiteList().contains(a.getAPICategory()))
				.anyMatch(a -> ! filterOutFeature(a));
	}

	@Override
	public boolean keepUniprotEntryXref() {
		return false;
	}
		
	/**
	 * Default implementation
	 * Subclasses should only override getXrefDbNameWhiteList() and optionally override keepUniprotEntryXref()
	 * 
	 * Computes the list of xrefs that should be displayed in an page view
	 * @param entry an entry build with everything !!!
	 * @return the list of xrefs to display in the page view
	 */
	@Override
	public List<DbXref> getFurtherExternalLinksXrefs(Entry entry) {
		
		// get a list of xrefs according to config
		List<DbXref> xrefs = 
				entry.getXrefs().stream()
				.filter(x -> getXrefDbNameWhiteList().contains(x.getDatabaseName()))
				.filter(x -> ! filterOutXref(x))
				.collect(Collectors.toList());
		
		// remove xrefs already mentioned in annotations 
		Set<Long> idsToRemove = AnnotationUtils.getXrefIdsForAnnotations(entry.getAnnotations());
		
		if (keepUniprotEntryXref()) {
			xrefs.stream()
				.filter(x -> x.getAccession().equals(entry.getUniprotName()) && x.getDatabaseName().equals("UniProt"))
				.findFirst()
				.ifPresent(x -> idsToRemove.remove(x.getDbXrefId()));
		
		}
		xrefs = XrefUtils.filterOutXrefsByIds(xrefs, idsToRemove);
		return xrefs;

	}

	/**
	 * (Not used yet)
	 * Default implementation 
	 * Subclasses should only override getAnnotationCategoryWhiteList() and optionally override filterOutAnnotation()
	 */
	@Override
	public List<Annotation> getAnnotationsForGenericAnnotationViewer(Entry entry) {
		return entry.getAnnotations().stream()
				.filter(a -> getAnnotationCategoryWhiteList().contains(a.getAPICategory()))
				.filter(a -> ! filterOutAnnotation(a))
				.collect(Collectors.toList());
	}

	/**
	 * (Not used yet)
	 * Default implementation 
	 * Subclasses should only override getFeatureCategoryWhiteList() and optionally override filterOutFeature()
	 */
	@Override
	public List<Annotation> getAnnotationsForTripleAnnotationViewer(Entry entry) {
		return entry.getAnnotations().stream()
				.filter(a -> getFeatureCategoryWhiteList().contains(a.getAPICategory()))
				.filter(a -> ! filterOutFeature(a))
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean doesDisplayAnnotationCategory(AnnotationCategory cat) {
		if (getFeatureCategoryWhiteList().contains(cat)) return true;
		if (getAnnotationCategoryWhiteList().contains(cat)) return true;
		return false;
	}
	
	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * Filter entry annotations based on any criteria
	 * @param annotation annotation category to test
	 * @return true if annotation category passes the filter
	 */
	protected boolean filterOutAnnotation(Annotation annotation) {
		return false;
	}

	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * Filter entry features based on any criteria
	 * @param feature feature category to test
	 * @return true if feature category passes the filter
	 */
	protected boolean filterOutFeature(Annotation feature) {
		return false;
	}

	/**
	 * Default implementation (subclasses should override this method if needed)
	 *
	 * Filter entry xrefs
	 * @param xref cross ref to test
	 * @return true if xref passes the filter
	 */
	protected boolean filterOutXref(DbXref xref) {
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
}
