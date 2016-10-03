package org.nextprot.api.web.ui;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.ui.page.BasePageDisplayRequirement;

import java.util.Map;

import static org.mockito.Matchers.any;

public class PageDisplayTesterTest {

    @Test
    public void shouldDisplayPage() throws Exception {

        PageDisplayTester pageDisplayTester = new PageDisplayTester(Mockito.mock(Entry.class));

        pageDisplayTester.addPageRequirement(mockPageConfig("mock", true));

        Map<String, Boolean> map = pageDisplayTester.testPageContent();

        Assert.assertTrue(map.values().stream().allMatch(Boolean::booleanValue));
    }

    @Test
    public void allPageNames() throws Exception {

        Assert.assertTrue(!PageDisplayTester.getAllTestingPageNames().isEmpty());
    }

    private static BasePageDisplayRequirement mockPageConfig(String pageName, boolean bool) {

        BasePageDisplayRequirement page = Mockito.mock(BasePageDisplayRequirement.class);

        Mockito.when(page.doDisplayPage(any(Entry.class))).thenReturn(bool);
        Mockito.when(page.getPageName()).thenReturn(pageName);

        return page;
    }
}