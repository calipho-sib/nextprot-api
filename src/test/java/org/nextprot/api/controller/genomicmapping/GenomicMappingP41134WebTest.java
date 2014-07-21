package org.nextprot.api.controller.genomicmapping;

import org.junit.Test;
import org.nextprot.api.dbunit.MVCDBUnitBaseTest;
import org.nextprot.api.utils.MockMVCUtils;

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
