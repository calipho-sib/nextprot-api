package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.ui.page.PageView;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.core.utils.XrefUtils;

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
		Objects.requireNonNull(getXrefDatabaseWhiteList(), "selected xref db name list should not be null");
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
				.anyMatch(xr -> getXrefDbNameWhiteList().contains(xr.getDatabaseName())))
			return true;

		// then annotations
		if (entry.getAnnotations().stream()
				.anyMatch(a -> getAnnotationCategoryWhiteList().contains(a.getAPICategory())))
			return true;

		// then features
		return entry.getAnnotations().stream()
				.anyMatch(a -> getFeatureCategoryWhiteList().contains(a.getAPICategory()));
	}

	@Override
	public boolean keepUniprotEntryXref() {
		return false;
	}
		
	@Override
	public boolean keepHpaENSGXrefs() {
		return true;
	}
		
	/**
	 * Default implementation
	 * Subclasses should only override getXrefDatabaseWhiteList() and optionally override keepUniprotEntryXref()
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
				.collect(Collectors.toList());

		// get list of annotations listed in the page
		List<Annotation> displayedAnnots = entry.getAnnotations().stream().
				filter(a -> doesDisplayAnnotationCategory(a.getAPICategory()))
				.collect(Collectors.toList());

		
		// remove xrefs already mentioned in annotations 
		Set<Long> idsToRemove = AnnotationUtils.getXrefIdsForAnnotations(displayedAnnots);
		if (keepUniprotEntryXref()) {
			xrefs.stream()
				.filter(x -> x.getAccession().equals(entry.getUniprotName()) && x.getDatabaseName().equals("UniProt"))
				.findFirst()
				.ifPresent(x -> idsToRemove.remove(x.getDbXrefId()));		
		}
		xrefs = XrefUtils.filterOutXrefsByIds(xrefs, idsToRemove);
		
		// remove xrefs from HPA for ENSG expression if requested
		if (! keepHpaENSGXrefs()) {
			xrefs = XrefUtils.filterOutHpaENSGXrefs(xrefs);
		}
		return xrefs;

	}

	/**
	 * Default implementation 
	 * Subclasses should only override getAnnotationCategoryWhiteList() and optionally override filterOutAnnotation()
	 */
	@Override
	public List<Annotation> getAnnotationsForGenericAnnotationViewer(Entry entry) {
		return entry.getAnnotations().stream()
				.filter(a -> getAnnotationCategoryWhiteList().contains(a.getAPICategory()))
				.collect(Collectors.toList());
	}

	/**
	 * Default implementation 
	 * Subclasses should only override getFeatureCategoryWhiteList() and optionally override filterOutFeature()
	 */
	@Override
	public List<Annotation> getAnnotationsForTripleAnnotationViewer(Entry entry) {
		return entry.getAnnotations().stream()
				.filter(a -> getFeatureCategoryWhiteList().contains(a.getAPICategory()))
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean doesDisplayAnnotationCategory(AnnotationCategory cat) {
		return getFeatureCategoryWhiteList().contains(cat) || getAnnotationCategoryWhiteList().contains(cat);
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
	@Nonnull protected abstract List<XrefDatabase> getXrefDatabaseWhiteList();

	private List<String> getXrefDbNameWhiteList() {

		return getXrefDatabaseWhiteList().stream()
				.map(db -> db.getName())
				.collect(Collectors.toList());
	}
}
