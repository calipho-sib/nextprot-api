package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.EntryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles({ "dev","cache" })
public class EntryUtilsTest extends CoreUnitBaseTest{
        
    @Autowired
	private EntryBuilderService entryBuilderService = null;
    
    @Test
	public void testGetFunctionInfoWithCanonicalFirst() {
     	List<String> FunctionInfoWithCanonicalFirst;
    	
        Entry testentry = entryBuilderService.build(EntryConfig.newConfig("NX_P46778").withAnnotations());
        FunctionInfoWithCanonicalFirst = EntryUtils.getFunctionInfoWithCanonicalFirst(testentry);
        Assert.assertEquals(1, FunctionInfoWithCanonicalFirst.size());

        testentry = entryBuilderService.build(EntryConfig.newConfig("NX_P19367").withAnnotations());
        FunctionInfoWithCanonicalFirst = EntryUtils.getFunctionInfoWithCanonicalFirst(testentry);
        Assert.assertTrue(FunctionInfoWithCanonicalFirst.contains("cellular glucose homeostasis"));
    }
}