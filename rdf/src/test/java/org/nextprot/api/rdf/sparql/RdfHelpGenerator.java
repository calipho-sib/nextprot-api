package org.nextprot.api.rdf.sparql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.rdf.domain.RdfTypeInfo;
import org.nextprot.api.rdf.service.RdfHelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ActiveProfiles({"dev,security"})
public class RdfHelpGenerator extends AbstractUnitBaseTest {

	@Autowired
	private RdfHelpService service;

	/**
	 * remove the Ignore annotation to run this task
	 */
	@Ignore
	@Test
	public void generatorTest() {
		List<RdfTypeInfo> list = this.service.getRdfTypeFullInfoList();
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			mapper.writeValue(new File("../web/src/main/webapp/assets/rdfhelp.json"), list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
