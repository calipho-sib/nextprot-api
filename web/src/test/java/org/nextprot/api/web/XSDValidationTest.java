package org.nextprot.api.web;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

import static org.junit.Assert.fail;

@Ignore
@ActiveProfiles(profiles = {"cache"})
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
			schema = factory.newSchema(new StreamSource(new File("src/main/webapp/nextprot-export-v1.xsd")));

			File f = new File("tmp.xml");
			StreamSource xmlFile = new StreamSource(f);
			/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
			exportService.streamResultsInXML(baos, "entry", new HashSet<String>(Arrays.asList(new String[] { "NX_P01308" })));

			String prettyXml = XmlPrettyPrintFilter.getPrettyXml(baos.toString());
			PrintWriter out = new PrintWriter(f);
			out.print(prettyXml);
			out.close();
			*/
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
}
