package org.nextprot.api.web.ttl.integration;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.velocity.Template;
import org.junit.Test;
import org.nextprot.api.web.NXVelocityContext;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Writer;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class EntryTTLValidationIntegrationTest extends WebIntegrationBaseTest {

	@Autowired
	VelocityConfig velocityConfig;

	@Test
	public void shouldBeAValidTurtleAndContainNoDollar() throws Exception {
		String fileName = "test.ttl";
		String ttlData = this.mockMvc.perform(get("/entry/NX_P06213.ttl")).andReturn().getResponse().getContentAsString();
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		addTurtlePrefixes(writer, velocityConfig);
		writer.write(ttlData);
		writer.close();
		RDFDataMgr.loadModel(fileName);
		assertTrue(checkNoDollarInFile(fileName));
		new File(fileName).delete();
	}

	private boolean checkNoDollarInFile(String fname) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(new File(fname)));
		 String line = null; 
		 int lineNo = 0;
		 boolean ok = true; 
		 while ((line = reader.readLine()) != null) {
			 lineNo++;
			 if (line.contains("$")) {
				 ok = false;
				 //System.out.println("Error - '$' found at line " + String.valueOf(lineNo) + line);
			 }
		 }
		 reader.close();
		 return ok;
	}
	
	static void addTurtlePrefixes(Writer writer, VelocityConfig config) {
		Template headerTemplate = config.getVelocityEngine().getTemplate("turtle/prefix.ttl.vm");
		headerTemplate.merge(new NXVelocityContext(), writer);
	}

}
