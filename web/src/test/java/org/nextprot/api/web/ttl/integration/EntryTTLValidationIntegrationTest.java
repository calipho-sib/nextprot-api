package org.nextprot.api.web.ttl.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.velocity.Template;
import org.junit.Test;
import org.nextprot.api.web.NXVelocityContext;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

public class EntryTTLValidationIntegrationTest extends WebIntegrationBaseTest {

	@Autowired
	VelocityConfig velocityConfig;

	@Test
	public void shouldBeAValidTurtle() throws Exception {
		String fileName = "test.ttl";
		String ttlData = this.mockMvc.perform(get("/entry/NX_P06213.ttl")).andReturn().getResponse().getContentAsString();
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		addTurtlePrefixes(writer, velocityConfig);
		writer.write(ttlData);
		writer.close();
		RDFDataMgr.loadModel("test.ttl");
		new File(fileName).delete();
	}

	static void addTurtlePrefixes(Writer writer, VelocityConfig config) {
		Template headerTemplate = config.getVelocityEngine().getTemplate("turtle/prefix.ttl.vm");
		headerTemplate.merge(new NXVelocityContext(), writer);
	}

}
