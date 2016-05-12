package org.nextprot.api.web.service;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.core.domain.phenotypes.PhenotypeAnnotation;
import org.nextprot.api.core.service.PhenotypeService;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */

public class PhenotypeServiceTest extends WebUnitBaseTest {

	@Autowired
	private PhenotypeService phenotypeService;

	@Test
	public void shouldFindPhenotypes() throws Exception {
		Map<String, List<PhenotypeAnnotation>>  phenotypes = phenotypeService.findPhenotypeAnnotations("brca");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		System.out.println(mapper.writeValueAsString(phenotypes));
	}

}
