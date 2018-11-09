package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ActiveProfiles({"dev"})
@ContextConfiguration("classpath:spring/solr-context.xml")
public class AnnotationSolrFieldCollectorTest extends AbstractUnitBaseTest {

    // Class under test
    @Autowired
    private AnnotationSolrFieldCollector annotationSolrFieldCollector;

	@Test
	public void testGetFunctionInfoWithCanonicalFirst() {

		List<String> FunctionInfoWithCanonicalFirst;

		FunctionInfoWithCanonicalFirst = annotationSolrFieldCollector.getFunctionInfoWithCanonicalFirst("NX_P46778");
		Assert.assertEquals(1, FunctionInfoWithCanonicalFirst.size());

		FunctionInfoWithCanonicalFirst = annotationSolrFieldCollector.getFunctionInfoWithCanonicalFirst("NX_P19367");
		Assert.assertTrue(FunctionInfoWithCanonicalFirst.contains("cellular glucose homeostasis"));
	}
}
