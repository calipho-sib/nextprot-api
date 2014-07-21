package org.nextprot.api.export.xsd;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.dao.MasterIdentifierDao;
import org.nextprot.api.dbunit.MVCBaseIntegrationTest;
import org.nextprot.api.domain.file.format.NPFileFormat;
import org.nextprot.api.export.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

public class XSDValidationTest extends MVCBaseIntegrationTest {

	private final Source SCHEMA_FILE = new StreamSource(new File("src/main/webapp/resources/nextprotExport.xsd"));

	@Autowired
	private ExportService exportService;
	@Autowired
	private MasterIdentifierDao midao;

	@Before
	public void clearRepository() {
		exportService.clearRepository();
	}

	@Test
	@Ignore
	public void shouldValidateXMLFilewithXSD() {

		List<String> uniqueNames = midao.findMasterSequenceUniqueNames();

		String currentUniqueName = "";
		try {

			for (String uniqueName : uniqueNames) {

				currentUniqueName = uniqueName;
				Future<File> futureFile = exportService.exportEntry(uniqueName, NPFileFormat.XML);

				// parse an XML document into a DOM tree
				DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = parser.parse(futureFile.get());

				// create a SchemaFactory capable of understanding WXS schemas
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

				// load a WXS schema, represented by a Schema instance

				Schema schema = factory.newSchema(SCHEMA_FILE);

				// create a Validator instance, which can be used to validate an instance document
				Validator validator = schema.newValidator();

				// validate the DOM tree
				validator.validate(new DOMSource(document));

				System.err.println("Ok for " + currentUniqueName);

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failure for " + currentUniqueName);
			System.err.println("See http://uat-web1:8080/db/search/export?ac=" + currentUniqueName + "&type=xml for comparison");
			fail();
		}

	}

}
