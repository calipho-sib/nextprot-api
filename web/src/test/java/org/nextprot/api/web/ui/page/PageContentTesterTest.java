package org.nextprot.api.web.ui.page;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Entry;

import java.util.Map;

import static org.mockito.Matchers.any;

public class PageContentTesterTest {

    @Test
    public void hasContent() throws Exception {

        PageContentTester pageContentTester = new PageContentTester(Mockito.mock(Entry.class));

        pageContentTester.addPage(mockPageConfig("mock", true));

        Map<String, Boolean> map = pageContentTester.testPageContent();

        Assert.assertTrue(map.values().stream().allMatch(Boolean::booleanValue));
    }

    @Test
    public void testAllPageNames() throws Exception {

        Assert.assertTrue(!PageContentTester.getAllTestablePageNames().isEmpty());
    }

    @Test
    public void allPagesShouldBeDisplayed() throws Exception {

        PageContentTester pageContentTester = PageContentTester.allPages(Mockito.mock(Entry.class));

        Map<String, Boolean> map = pageContentTester.testPageContent();

        Assert.assertTrue(map.values().stream().allMatch(Boolean::booleanValue));
    }

    private static SimplePageConfig mockPageConfig(String pageName, boolean bool) {

        SimplePageConfig page = Mockito.mock(SimplePageConfig.class);

        Mockito.when(page.hasContent(any(Entry.class))).thenReturn(bool);
        Mockito.when(page.getPageName()).thenReturn(pageName);

        return page;
    }
}