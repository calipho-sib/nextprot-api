package org.nextprot.api.core.domain.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides useful methods to retrieve entry data for a given entry page view
 *
 * Created  by fnikitin on 03/10/16.
 * Extended by pam      on 10/08/17
 */
public interface PageView {

	String getLabel();

	String getLink();

    /**
     * Check entry content for current page display
     * @param entry the entry to check content
     * @return true if page should be display
     */
    boolean doDisplayPage(@Nonnull Entry entry);

    /**
     * Build a list of annotations suitable for the generic annotation viewer
     * @param entry an Entry built with everything (all annotations, all xrefs at least)
     * @return
     */
	List<Annotation> getAnnotationsForGenericAnnotationViewer(Entry entry);

	/**
     * Build a list of annotations suitable for the triple viewer 
     * @param entry an Entry built with everything (all annotations, all xrefs at least)
	 * @return a list of positional annotations
	 */
	List<Annotation> getAnnotationsForTripleAnnotationViewer(Entry entry);

	/**
     * Build a list of xrefs suitable for the further external link section
     * @param entry an Entry built with everything (all annotations, all xrefs at least)
	 * @return a list of xrefs
	 */
	List<DbXref> getFurtherExternalLinksXrefs(Entry entry);

	/**
	 * Used by getFurtherExternalLinksXrefs()
	 * @return false in default implementation, only Sequence page should override it and set to true
	 */
	boolean keepUniprotEntryXref();

	/**
	 * Used by getFurtherExternalLinksXrefs()
	 * @return true in default implementation, only Proteomics page should override it and set to false
	 */
	boolean keepHpaENSGXrefs();

	/**
	 * Used by EntryPublicationUtils to build the "Cited for" field content
	 * @param annotationCategory
	 * @return true it the view displays the annotation category either in the generic annotation viewer or in the feature triple viewer
	 */
	boolean doesDisplayAnnotationCategory(AnnotationCategory annotationCategory);

    /**
     * @return the set of page views able to display this annotation
     */
	static Set<PageView> getDisplayablePageViews(Annotation annotation) {

	    return PageViewFactory.getPageViews().stream()
                .filter(pageView -> pageView.doesDisplayAnnotationCategory(annotation.getAPICategory()))
                .collect(Collectors.toSet());
    }
}
