package org.nextprot.api.web.ui.page;

import org.nextprot.api.core.domain.Entry;

import javax.annotation.Nonnull;

/**
 * Test if page should be displayed for entry
 *
 * Created by fnikitin on 03/10/16.
 */
public interface PageDisplayRequirement {

    /**
     * Check entry content for current page display
     * @param entry the entry to check content
     * @return true if page should be display
     */
    boolean doDisplayPage(@Nonnull Entry entry);

    /**
     * @return page
     */
    EntryPage getPage();
}
