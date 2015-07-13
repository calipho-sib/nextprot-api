package org.nextprot.api.web.txt.unit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.impl.NPStreamExporter;
import org.nextprot.api.web.service.impl.TXTStreamExporter;

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
        NPStreamExporter exporter = new TXTStreamExporter();
        exporter.setEntryBuilderService(entryBuilderMockService);

        Map<String, Object> map = new HashMap<String, Object>(); map.put(ExportService.ENTRIES_COUNT_PARAM, 2);
        exporter.export(Arrays.asList("NX_1", "NX_2"), writer, "entry", map);
        when(entryBuilderMockService.build(any(EntryConfig.class))).thenReturn(new Entry("NX_1")).thenReturn(new Entry("NX_2"));
       
        String[] rows = out.toString().split("\n");
        assertEquals(rows[0], "#nb entries=2");
        assertEquals(rows[1], "NX_1");
        assertEquals(rows[2], "NX_2");
        
    }

}
