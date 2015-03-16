package org.nextprot.api.rdf.sparql;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.rdf.domain.RdfTypeInfo;
import org.nextprot.api.rdf.service.RdfHelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"dev"})
public class RdfHelpGenerator extends AbstractUnitBaseTest {

	@Autowired
	private RdfHelpService service;

	@Test
	public void generatorTest() {
		//service.getRdfTypeFullInfo(":Repeat");
		List<RdfTypeInfo> list = this.service.getRdfTypeFullInfoList();
		//this.service.getRdfTypeFullInfoList();
	}


}
