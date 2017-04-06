package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import org.nextprot.api.core.domain.EntryUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ActiveProfiles({ "dev" })
public class EntryUtilsTest extends CoreUnitBaseTest{
		
    public static Entry mockEntry(String accession, Isoform... isoforms) {

        Entry entry = Mockito.mock(Entry.class);

        when(entry.getUniqueName()).thenReturn(accession);

        if (isoforms.length > 0) {
            when(entry.getIsoforms()).thenReturn(Arrays.asList(isoforms));
        }

        return entry;
    }

    public static Isoform mockIsoform(String accession, String name, boolean canonical) {

        Isoform isoform = Mockito.mock(Isoform.class);
        when(isoform.getUniqueName()).thenReturn(accession);
        when(isoform.isCanonicalIsoform()).thenReturn(canonical);

        EntityName entityName = Mockito.mock(EntityName.class);
        when(entityName.getName()).thenReturn(name);

        when(isoform.getMainEntityName()).thenReturn(entityName);

        return isoform;
    }

    public static Isoform mockIsoform(String accession, String name, boolean canonical, String sequence) {

        Isoform isoform = mockIsoform(accession, name, canonical);
        when(isoform.getSequence()).thenReturn(sequence);

        return isoform;
    }
        
    @Autowired
	private EntryBuilderService entryBuilderService = null;
    
    @Test // TODO: this is the only real test in this file, mockito stuff is rather an utility and should be moved elsewhere, without the CoreUnitBaseTest extension
	public void testGetFunctionInfoWithCanonicalFirst() {
     	List<String> FunctionInfoWithCanonicalFirst;
    	
    		Entry testentry = entryBuilderService.build(EntryConfig.newConfig("NX_P46778").withEverything());
    		FunctionInfoWithCanonicalFirst = EntryUtils.getFunctionInfoWithCanonicalFirst(testentry);
    		Assert.assertEquals(3, FunctionInfoWithCanonicalFirst.size());
    		
    		testentry = entryBuilderService.build(EntryConfig.newConfig("NX_P19367").withEverything());
    		FunctionInfoWithCanonicalFirst = EntryUtils.getFunctionInfoWithCanonicalFirst(testentry);
    		Assert.assertEquals("cellular glucose homeostasis",FunctionInfoWithCanonicalFirst.get(0));
    }
}