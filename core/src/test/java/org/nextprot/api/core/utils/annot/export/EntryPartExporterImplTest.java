package org.nextprot.api.core.utils.annot.export;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.nextprot.api.core.utils.annot.export.EntryPartExporter.*;

import java.util.List;

@ActiveProfiles({ "dev" })
public class EntryPartExporterImplTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Test
    public void getDefaultHeaders() throws Exception {

        EntryPartExporter exporter = new EntryPartExporterImpl.Builder().build();

        List<Header> headers = exporter.exportHeaders();
        Assert.assertEquals(10, headers.size());
    }

    @Test
    public void add1Column() throws Exception {

        EntryPartExporter exporter = new EntryPartExporterImpl.Builder().addColumns(EntryPartExporter.Header.CELL_LINE_ACCESSION).build();

        List<Header> headers = exporter.exportHeaders();
        Assert.assertEquals(11, headers.size());
    }

    @Test
    public void add1AlreadyExistingColumn() throws Exception {

        EntryPartExporter exporter = new EntryPartExporterImpl.Builder().addColumns(Header.ENTRY_ACCESSION).build();

        List<Header> headers = exporter.exportHeaders();
        Assert.assertEquals(10, headers.size());
    }

    @Test
    public void getColumnsForEntryNX_P01308() throws Exception {

        EntryPartExporter exporter = new EntryPartExporterImpl.Builder()
                .addColumns(Header.EXPRESSION_LEVEL, Header.STAGE_ACCESSION, Header.STAGE_NAME).build();

        String subpart = "expression-profile";
        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").with(subpart));

        List<Row> rows = exporter.exportRows(entry);
        Assert.assertEquals(533, rows.size());
        Assert.assertEquals(13, rows.get(0).size());
    }
}