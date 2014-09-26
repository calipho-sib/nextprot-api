package org.nextprot.api.core.controller.genomicmapping;

import org.junit.Test;
import org.nextprot.api.commons.utils.MockMVCUtils;
import org.nextprot.api.core.dbunit.MVCDBUnitBaseTest;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Testing the template for interactions
 * 
 * @author dteixeira
 */
@DatabaseSetup(value = "GenomicMappingP41134Test.xml", type = DatabaseOperation.INSERT)
public class GenomicMappingP41134WebTest extends MVCDBUnitBaseTest {

	@Test
	public void shouldGetAMonoExon() throws Exception {

		MockMVCUtils.assertWebContent(mockMvc, "/entry/NX_P41134/genomic/genomic-mapping.xml", "src/test/resources/org/nextprot/api/controller/genomicmapping/GenomicMappingP41134Result.xml");

	}

}
