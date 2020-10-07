package org.nextprot.api.core.export;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.export.EntryPartExporter.Header;
import org.nextprot.api.core.export.EntryPartExporter.Row;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@ActiveProfiles({ "dev","cache" })
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
        
/*        System.out.println("rows:" + rows.size());
        System.out.println("cols:" + rows.get(0).size());
        for (Row r: rows) {
        	for (int i=0;i<r.size();i++) {
        		System.out.print(r.getValue(i) + "\t");
        	}
        	System.out.println("");
        }
*/        
        Assert.assertTrue(rows.size() > 150);
        Assert.assertEquals(13, rows.get(0).size());
    }
    

    @Test
    public void testPartialSortOrderForExpressionProfile() throws Exception {

        // the sort order defined fro expression-profile is :
        // alpha(term name), alpha(eco_name), alpha(expression level), temporal(stage accession) 
        // here we test that the 3 first criteria are correct

    	String subpart = "expression-profile";
        EntryPartExporter exporter = EntryPartExporterImpl.fromSubPart(subpart);
        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").with(subpart));
        List<Row> rows = exporter.exportRows(entry);
        String lastSortKey="";
        for (Row r : rows) {
        	String termName = r.getValue(exporter.getColumnIndex(Header.TERM_NAME));
        	String ecoName = r.getValue(exporter.getColumnIndex(Header.ECO_NAME));
        	String exprLevel = r.getValue(exporter.getColumnIndex(Header.EXPRESSION_LEVEL));
        	String sortKey = termName + "\t" + ecoName + "\t" + exprLevel; 
        	int result = lastSortKey.compareTo(sortKey);
        	//System.out.println("result:"+ result + "for: " + sortKey); //  + " " +  stage + " -" + stageAc);
        	Assert.assertTrue(result <= 0);
        	lastSortKey = sortKey;
        }
    }
    

}