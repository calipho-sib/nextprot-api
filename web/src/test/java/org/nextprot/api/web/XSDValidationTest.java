package org.nextprot.api.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.commons.utils.XMLPrettyPrinter;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.StreamEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.fail;

@ActiveProfiles()
public class XSDValidationTest extends WebIntegrationBaseTest {


	@Autowired
	private ExportService exportService;

	@Autowired
	private StreamEntryService streamEntryService;

	@Before
	public void clearRepository() {
		exportService.clearRepository();
	}

	@Test
	public void shouldValidateNX_Q15858XMLFilewithXSD() {

        shouldValidateXMLFilewithXSD("NX_Q15858");
	}

    @Test
    public void shouldValidateMultipleEntriesXMLFilewithXSD() {

        Stream.of("NX_O43521", "NX_O95084", "NX_P0DMV8", "NX_P31947", "NX_P51522", "NX_P80723", "NX_Q6ZMV5",
                "NX_Q8IXZ3", "NX_Q8IYU4", "NX_Q8N6I1", "NX_Q8TCT1", "NX_Q8WZ71", "NX_Q96CX3", "NX_Q9BRL6",
                "NX_Q9UKF2", "NX_Q9Y6B2", "NX_Q9Y6Z5").forEach(entryAccession -> shouldValidateXMLFilewithXSD(entryAccession));
    }

	@Test
	public void mostOfAnnotationCategoriesShouldBePresentInXSD() throws IOException {

		List<String> lines = Files.readAllLines(Paths.get("src/main/webapp/nextprot-export-v2.xsd"));

		for (AnnotationCategory ac : AnnotationCategory.values()) {

			if (ac.isLeaf()
					&& ac != AnnotationCategory.FAMILY_NAME
					//&& ac != AnnotationCategory.ELECTROPHYSIOLOGICAL_PARAMETER
					&& ac != AnnotationCategory.PEPX_VIRTUAL_ANNOTATION) {

				String camelCaseCategoryName = StringUtils.camelToKebabCase(ac.getApiTypeName());

				Assert.assertTrue("Should find category " + camelCaseCategoryName,
						lines.stream().anyMatch(line -> line.contains(camelCaseCategoryName)));
			}
		}
	}

    private void shouldValidateXMLFilewithXSD(String entryAccession) {

        Schema schema;
        try {

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schema = factory.newSchema(new StreamSource(new File("src/main/webapp/nextprot-export-v2.xsd")));

            File f = new File("tmp.xml");
            StreamSource xmlFile = new StreamSource(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            streamEntryService.streamEntries(Collections.singletonList(entryAccession), NextprotMediaType.XML, "entry", baos, "");

            XMLPrettyPrinter prettyPrinter = new XMLPrettyPrinter();

            String prettyXml = prettyPrinter.prettify(baos.toString());
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
}
