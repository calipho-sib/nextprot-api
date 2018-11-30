package org.nextprot.api.web.service.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ui.page.PageView;
import org.nextprot.api.core.domain.ui.page.impl.SequencePageView;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

public class PageViewTest extends WebIntegrationBaseTest {

	@Autowired
	private EntryBuilderService entryBuilderService;

    @Ignore
    @Test
    public void shouldDoIt() throws Exception {
    	//String entryName="NX_Q8WZ42";  // TITIN, 1st most annotated protein: 147ms processing
    	//String entryName="NX_Q5VST9";  // OBSCN, 3rd most annotated protein:  25ms processing
    	String entryName="NX_P02649";  
    	//String entryName="NX_P52701";
    	//String entryName="NX_P01308";
    	
    	Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());
//    	System.out.println("- - - - - before - - - - -");
//    	System.out.println("annot categ : " + entry.getAnnotationsByCategory().size());
//    	System.out.println("annot count : " + entry.getAnnotations().size());
//    	System.out.println("xrefs       : " + entry.getXrefs().size());
    	showXref(entry, "UniProt");
    	
    	PageView pageDef = new SequencePageView();
    	long t0 = System.currentTimeMillis();
    	List<DbXref> xrefs = pageDef.getFurtherExternalLinksXrefs(entry);
    	t0 = System.currentTimeMillis()-t0;
    	//System.out.println("- - - - - after  - - - - -");
    	showXref(xrefs);
//    	System.out.println("xrefs       : " + xrefs.size());
//    	System.out.println("proc time ms: " + t0);
//    	System.out.println("- - - - - end  - - - - -");
    }

    private static class MyDbXrefComparator implements Comparator<DbXref> {
		@Override
		public int compare(DbXref o1, DbXref o2) {
			int crit0 = o1.getDatabaseCategory().compareTo(o2.getDatabaseCategory());
			if (crit0 !=0) return crit0;
			int crit1 = o1.getDatabaseName().toLowerCase().compareTo(o2.getDatabaseName().toLowerCase());
			if (crit1 != 0) return crit1;
			int crit2 = o1.getAccession().toLowerCase().compareTo(o2.getAccession().toLowerCase());
			if (crit2 != 0) return crit2;
			return o1.getDbXrefId().compareTo(o2.getDbXrefId());
		}
    }	
    
    private void showXref(List<DbXref> xrefs) {
    	xrefs.stream()
    	.sorted(new MyDbXrefComparator())
    	.forEach(x -> showXref(x));	
    }
    
    private void showXref(Entry entry, String dbName) {
    	entry.getXrefs().stream()
    		.filter(x -> x.getDatabaseName().equals(dbName))
    		.forEach(x -> showXref(x));
    }
    
    private void showXref(DbXref x) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(x.getDatabaseCategory()).append(" - ");
    	sb.append(x.getDatabaseName()).append(" - ");
    	sb.append(x.getAccession()).append(" - ");
    	sb.append(x.getDbXrefId()).append(" - ");
    	if (x.getProperties()!=null && ! x.getProperties().isEmpty()) {
    		x.getProperties().stream()
    		.forEach(p -> sb.append("[").append(p.getName()).append("=").append(p.getValue()).append(" ").append("]"));
    	}
    	//System.out.println(sb.toString());
    }

}