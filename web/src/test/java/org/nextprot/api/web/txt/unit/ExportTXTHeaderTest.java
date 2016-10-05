package org.nextprot.api.web.txt.unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.impl.writer.EntryVelocityBasedStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryTXTStreamWriter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Adding test for txt export (including header)
 * 
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class ExportTXTHeaderTest extends WebUnitBaseTest {
	
    @Mock
    EntryBuilderService entryBuilderMockService;

	@Before
	public void setup() {
		super.setup();
		MockitoAnnotations.initMocks(this);
	}

    //TEST for https://issues.isb-sib.ch/browse/CALIPHOMISC-330
    @Test
    public void shouldContainTheNumberOfEntriesInTheHeader() throws Exception {
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);
        EntryVelocityBasedStreamWriter exporter = new EntryTXTStreamWriter(writer);
        exporter.setEntryBuilderService(entryBuilderMockService);

        Map<String, Object> map = new HashMap<>(); map.put(ExportService.ENTRIES_COUNT_PARAM, 2);
        when(entryBuilderMockService.build(any(EntryConfig.class))).thenReturn(new Entry("NX_1")).thenReturn(new Entry("NX_2"));
        exporter.write(Arrays.asList("NX_1", "NX_2"), map);
       
        String[] rows = out.toString().split(StringUtils.CR_LF);
        assertEquals(rows[0], "#nb entries=2");
        assertEquals(rows[1], "NX_1");
        assertEquals(rows[2], "NX_2");
        
    }

}
