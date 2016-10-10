package org.nextprot.api.web.ui.page;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Entry;

import java.util.Map;

import static org.mockito.Matchers.any;

public class PageDisplayReportTest {

    @Test
    public void shouldDisplayPage() throws Exception {

        PageDisplayReport pageDisplayReport = new PageDisplayReport();

        pageDisplayReport.addPredicate(mockPageConfig(EntryPage.FUNCTION, true));
        Map<String, Boolean> map = pageDisplayReport.reportDisplayPageStatus(Mockito.mock(Entry.class));

        Assert.assertTrue(map.values().stream().allMatch(Boolean::booleanValue));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotMultiplePageRequirementForSameEntryPage() throws Exception {

        PageDisplayReport pageDisplayReport = new PageDisplayReport();

        pageDisplayReport.addPredicate(mockPageConfig(EntryPage.FUNCTION, true));
        pageDisplayReport.addPredicate(mockPageConfig(EntryPage.FUNCTION, true));
    }

    private static PageDisplayPredicate mockPageConfig(EntryPage entryPage, boolean bool) {

        PageDisplayPredicate page = Mockito.mock(PageDisplayPredicate.class);

        Mockito.when(page.doDisplayPage(any(Entry.class))).thenReturn(bool);
        Mockito.when(page.getPage()).thenReturn(entryPage);

        return page;
    }
}