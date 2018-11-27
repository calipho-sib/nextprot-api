package org.nextprot.api.web.service.impl.writer;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@Ignore
public class EntryXMLStreamWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testXMLExportStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);
        EntryXMLStreamWriter exporter = new EntryXMLStreamWriter(writer, "overview", wac);
        exporter.write(Arrays.asList("NX_P06213", "NX_P01308"));
        exporter.close();
        writer.close();
        out.close();

        NodeList recommendedNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/entry-list/entry/overview/gene-list/gene/gene-name[@type='primary']");
        assertEquals(recommendedNodes.item(0).getTextContent(), "INSR");
        assertEquals(recommendedNodes.item(1).getTextContent(), "INS");
    }

	// NOTE: <chain> in <chain-list> is not steadily sorted by <chain-name>
	// As a workaround, we choose to test outputs with 2 alternative order
	// TODO: A fix/decision should be made in Overview.EntityName compareTo() method to make this comparison deterministic
    @Test
    public void testWriteXML() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntryXMLStreamWriter writer = new EntryXMLStreamWriter(out, "overview", wac);
        writer.write(Arrays.asList("NX_P06213", "NX_P01308"));
        writer.close();
        out.close();

        assertExpectedOutput(out.toString());
    }

    @Test
    public void testWriteXMLPrematureClose() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntryXMLStreamWriter writer = new EntryXMLStreamWriter(out, "overview", wac);
        writer.close();
        writer.write(Arrays.asList("NX_P06213"));
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<nextprot-export>\n" +
                "    <header>\n" +
                "        <database-name>neXtProt</database-name>\n" +
                "        <number-of-entries>$entriesCount</number-of-entries>\n" +
                "    </header>\n" +
                "    <entry-list>\n" +
                "    </entry-list>\n" +
                "    <copyright>\n" +
                "        Copyrighted by the SIB Swiss Institute of Bioinformatics.\n" +
                "        Distributed under the Creative Commons Attribution 4.0 International Public License (CC BY 4.0) - see https://creativecommons.org/licenses/by/4.0/\n" +
                "    </copyright>\n" +
                "</nextprot-export>\n", out.toString());
    }

    private static void assertExpectedOutput(String out) {

        String expectedXmlPrefixOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<nextprot-export>\n" +
                "    <header>\n" +
                "        <database-name>neXtProt</database-name>\n" +
                "        <number-of-entries>$entriesCount</number-of-entries>\n" +
                "        <release>\n" +
                "            <nextprot>\n" +
                "                <database-release>$release.databaseRelease</database-release>\n" +
                "                <api-release>$release.apiRelease</api-release>\n" +
                "            </nextprot>\n" +
                "            <data-source-list/>\n" +
                "        </release>\n" +
                "    </header>\n" +
                "    <entry-list>\n" +
                "        <entry accession=\"NX_P06213\" database=\"neXtProt\">\n" +
                "            <overview>\n" +
                "                <protein-existence value=\"Evidence_at_protein_level\"/>\n" +
                "                <protein-name-list>\n" +
                "                    <recommended-name>\n" +
                "                        <protein-name qualifier=\"full\">Insulin receptor</protein-name>\n" +
                "                        <protein-name qualifier=\"short\">IR</protein-name>\n" +
                "                        <protein-name qualifier=\"EC\">2.7.10.1</protein-name>\n" +
                "                    </recommended-name>\n" +
                "                    <alternative-name-list>\n" +
                "                        <alternative-name>\n" +
                "                            <protein-name qualifier=\"CD antigen\">CD220</protein-name>\n" +
                "                        </alternative-name>\n" +
                "                    </alternative-name-list>\n" +
                "                </protein-name-list>\n" +
                "                <chain-list>\n" +
                "                    <chain>\n" +
                "                        <recommended-name>\n" +
                "                            <chain-name qualifier=\"full\">Insulin receptor subunit alpha</chain-name>\n" +
                "                        </recommended-name>\n" +
                "                    </chain>\n" +
                "                    <chain>\n" +
                "                        <recommended-name>\n" +
                "                            <chain-name qualifier=\"full\">Insulin receptor subunit beta</chain-name>\n" +
                "                        </recommended-name>\n" +
                "                    </chain>\n" +
                "                </chain-list>\n" +
                "                <gene-list>\n" +
                "                    <gene>\n" +
                "                        <gene-name type=\"primary\">INSR</gene-name>\n" +
                "                    </gene>\n" +
                "                </gene-list>\n" +
                "                <family-list>\n" +
                "                    <family family-type=\"Superfamily\">\n" +
                "                        <cv-term accession=\"FA-03057\" terminology=\"uniprot-family-cv\">Protein kinase</cv-term>\n" +
                "                        <family family-type=\"Family\">\n" +
                "                            <cv-term accession=\"FA-03117\" terminology=\"uniprot-family-cv\">Tyr protein kinase</cv-term>\n" +
                "                            <family family-type=\"Subfamily\">\n" +
                "                                <cv-term accession=\"FA-03128\" terminology=\"uniprot-family-cv\">Insulin receptor</cv-term>\n" +
                "                            </family>\n" +
                "                        </family>\n" +
                "                    </family>\n" +
                "                </family-list>\n" +
                "                <history>\n" +
                "                    <entry-history database=\"neXtProt\" integrated=\"2010-03-22\" updated=\"2015-09-06\"/>\n" +
                "                    <entry-history database=\"UniProtKB\" integrated=\"1988-01-01\" updated=\"2015-09-16\" version=\"217\" last-sequence-update=\"2010-10-05\" sequence-version=\"4\"/>\n" +
                "                </history>\n" +
                "            </overview>\n" +
                "        </entry>\n" +
                "        <entry accession=\"NX_P01308\" database=\"neXtProt\">\n" +
                "            <overview>\n" +
                "                <protein-existence value=\"Evidence_at_protein_level\"/>\n" +
                "                <protein-name-list>\n" +
                "                    <recommended-name>\n" +
                "                        <protein-name qualifier=\"full\">Insulin</protein-name>\n" +
                "                    </recommended-name>\n" +
                "                    <alternative-name-list/>\n" +
                "                </protein-name-list>\n" +
                "                <chain-list>\n";

        String expectedXmlSuffixOutput =
                "                </chain-list>\n" +
                "                <gene-list>\n" +
                "                    <gene>\n" +
                "                        <gene-name type=\"primary\">INS</gene-name>\n" +
                "                    </gene>\n" +
                "                </gene-list>\n" +
                "                <family-list>\n" +
                "                    <family family-type=\"Family\">\n" +
                "                        <cv-term accession=\"FA-01869\" terminology=\"uniprot-family-cv\">Insulin</cv-term>\n" +
                "                    </family>\n" +
                "                </family-list>\n" +
                "                <history>\n" +
                "                    <entry-history database=\"neXtProt\" integrated=\"2010-03-01\" updated=\"2015-09-06\"/>\n" +
                "                    <entry-history database=\"UniProtKB\" integrated=\"1986-07-21\" updated=\"2015-09-16\" version=\"207\" last-sequence-update=\"1986-07-21\" sequence-version=\"1\"/>\n" +
                "                </history>\n" +
                "            </overview>\n" +
                "        </entry>\n" +
                "    </entry-list>\n" +
                "    <copyright>\n" +
                "        Copyrighted by the SIB Swiss Institute of Bioinformatics.\n" +
                "        Distributed under the Creative Commons Attribution 4.0 International Public License (CC BY 4.0) - see https://creativecommons.org/licenses/by/4.0/\n" +
                "    </copyright>\n" +
                "</nextprot-export>\n";

        String expectedXmlOutputAlt1 = expectedXmlPrefixOutput +
                "                    <chain>\n" +
                "                        <recommended-name>\n" +
                "                            <chain-name qualifier=\"full\">Insulin B chain</chain-name>\n" +
                "                        </recommended-name>\n" +
                "                    </chain>\n" +
                "                    <chain>\n" +
                "                        <recommended-name>\n" +
                "                            <chain-name qualifier=\"full\">Insulin A chain</chain-name>\n" +
                "                        </recommended-name>\n" +
                "                    </chain>\n" +
                expectedXmlSuffixOutput;

        String expectedXmlOutputAlt2 = expectedXmlPrefixOutput +
                "                    <chain>\n" +
                "                        <recommended-name>\n" +
                "                            <chain-name qualifier=\"full\">Insulin A chain</chain-name>\n" +
                "                        </recommended-name>\n" +
                "                    </chain>\n" +
                "                    <chain>\n" +
                "                        <recommended-name>\n" +
                "                            <chain-name qualifier=\"full\">Insulin B chain</chain-name>\n" +
                "                        </recommended-name>\n" +
                "                    </chain>\n" +
                expectedXmlSuffixOutput;


        Assert.assertTrue(expectedXmlOutputAlt1.equals(out.toString()) || expectedXmlOutputAlt2.equals(out.toString()));
    }
}