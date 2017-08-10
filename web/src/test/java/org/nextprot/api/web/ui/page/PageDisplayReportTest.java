package org.nextprot.api.web.ui.page;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Entry;

import java.util.Map;

public class PageDisplayReportTest {
	
    @Test
    public void shouldDisplayPage() throws Exception {

        PageDisplayReport pageDisplayReport = new PageDisplayReport();

        Map<String, Boolean> map = pageDisplayReport.reportDisplayPageStatus(Mockito.mock(Entry.class));

        Assert.assertTrue(map.values().stream().allMatch(Boolean::booleanValue));
    }
}