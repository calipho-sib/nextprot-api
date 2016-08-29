package org.nextprot.api.web;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.utils.XMLPrettyPrinter;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.impl.writer.NPEntryStreamWriter;
import org.nextprot.api.web.service.impl.writer.NPEntryXMLStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"cache"})
@Ignore
public class XSDValidationTest extends WebIntegrationBaseTest {


	@Autowired
	private ExportService exportService;

	@Before
	public void clearRepository() {
		exportService.clearRepository();
	}

	@Test
	public void shouldValidateXMLFilewithXSD() {

		
		Schema schema;
		try {

			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(new StreamSource(new File("src/main/webapp/nextprot-export-v2.xsd")));

			File f = new File("tmp.xml");
			StreamSource xmlFile = new StreamSource(f);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
            NPEntryStreamWriter<?> writer = new NPEntryXMLStreamWriter(baos, "entry");
			exportService.streamResults(writer, "entry", Arrays.asList(new String[] { "NX_Q15858" }));

			XMLPrettyPrinter prettyPrinter = new XMLPrettyPrinter();
			
			System.err.println(baos.toString());
			
			String prettyXml = prettyPrinter.prettify(baos.toString());
			System.out.println(prettyXml);
			PrintWriter out = new PrintWriter(f);
			out.print(prettyXml);
			out.close();
			
			// instance document
			Validator validator = schema.newValidator();
			// validate the DOM tree
			validator.validate(xmlFile);

			f.delete();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
			
		}


	}
	/*
	

	@Test
	public void shouldValidateXMLFilewithXSD() throws Exception {


			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(new File("src/main/webapp/nextprot-export-v2.xsd")));

			String xmlContent = this.mockMvc.perform(get("/entry/NX_Q15858.xml")).andReturn().getResponse().getContentAsString();

		    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    Document document = parser.parse(xmlContent);
		    // create a SchemaFactory capable of understanding WXS schemas
		
		    // create a Validator instance, which can be used to validate an instance document
		    Validator validator = schema.newValidator();

		    // validate the DOM tree
		    try {
		        validator.validate(new DOMSource(document));
		    } catch (SAXException e) {
				fail();
		    	// instance document is invalid!
		    }



	}*/
}
