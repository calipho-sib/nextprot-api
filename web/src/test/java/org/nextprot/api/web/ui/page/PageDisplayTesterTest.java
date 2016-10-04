package org.nextprot.api.web.ui.page;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Entry;

import java.util.Map;

import static org.mockito.Matchers.any;

public class PageDisplayTesterTest {

    @Test
    public void shouldDisplayPage() throws Exception {

        PageDisplayTester pageDisplayTester = new PageDisplayTester(Mockito.mock(Entry.class));

        pageDisplayTester.addRequirement(mockPageConfig(EntryPage.FUNCTION, true));

        Map<String, Boolean> map = pageDisplayTester.doDisplayPages();

        Assert.assertTrue(map.values().stream().allMatch(Boolean::booleanValue));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotMultiplePageRequirementForSameEntryPage() throws Exception {

        PageDisplayTester pageDisplayTester = new PageDisplayTester(Mockito.mock(Entry.class));

        pageDisplayTester.addRequirement(mockPageConfig(EntryPage.FUNCTION, true));
        pageDisplayTester.addRequirement(mockPageConfig(EntryPage.FUNCTION, true));
    }

    private static PageDisplayRequirement mockPageConfig(EntryPage entryPage, boolean bool) {

        PageDisplayRequirement page = Mockito.mock(PageDisplayRequirement.class);

        Mockito.when(page.doDisplayPage(any(Entry.class))).thenReturn(bool);
        Mockito.when(page.getPage()).thenReturn(entryPage);

        return page;
    }
}