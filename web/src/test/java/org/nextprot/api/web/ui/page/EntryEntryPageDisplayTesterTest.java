package org.nextprot.api.web.ui.page;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Entry;

import java.util.Map;

import static org.mockito.Matchers.any;

public class EntryEntryPageDisplayTesterTest {

    @Test
    public void shouldDisplayPage() throws Exception {

        EntryPageDisplayTester entryPageDisplayTester = new EntryPageDisplayTester(Mockito.mock(Entry.class));

        entryPageDisplayTester.addRequirement(mockPageConfig(EntryPage.FUNCTION, true));

        Map<String, Boolean> map = entryPageDisplayTester.doDisplayPages();

        Assert.assertTrue(map.values().stream().allMatch(Boolean::booleanValue));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotMultiplePageRequirementForSameEntryPage() throws Exception {

        EntryPageDisplayTester entryPageDisplayTester = new EntryPageDisplayTester(Mockito.mock(Entry.class));

        entryPageDisplayTester.addRequirement(mockPageConfig(EntryPage.FUNCTION, true));
        entryPageDisplayTester.addRequirement(mockPageConfig(EntryPage.FUNCTION, true));
    }

    private static PageDisplayRequirement mockPageConfig(EntryPage entryPage, boolean bool) {

        PageDisplayRequirement page = Mockito.mock(PageDisplayRequirement.class);

        Mockito.when(page.doDisplayPage(any(Entry.class))).thenReturn(bool);
        Mockito.when(page.getPage()).thenReturn(entryPage);

        return page;
    }
}