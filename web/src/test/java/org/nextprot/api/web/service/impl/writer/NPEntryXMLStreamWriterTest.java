package org.nextprot.api.web.service.impl.writer;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by fnikitin on 12/08/15.
 */
public class NPEntryXMLStreamWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testXMLExportStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);
        NPEntryXMLStreamWriter exporter = new NPEntryXMLStreamWriter(writer, "overview");
        exporter.write(Arrays.asList("NX_P06213", "NX_P01308"));
        exporter.close();
        writer.close();
        out.close();

        NodeList recommendedNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/entry-list/entry/overview/gene-list/gene/gene-name[@type='primary']");
        assertEquals(recommendedNodes.item(0).getTextContent(), "INSR");
        assertEquals(recommendedNodes.item(1).getTextContent(), "INS");
    }

    @Test
    public void testWriteXML() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NPEntryXMLStreamWriter writer = new NPEntryXMLStreamWriter(out, "overview");
        writer.write(Arrays.asList("NX_P06213", "NX_P01308"));
        writer.close();
        out.close();
        
        String expectedXmlOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
        		"                    <family>\n" + 
        		"                        <family-name-list>\n" + 
        		"                            <family-name type=\"Superfamily\" accession=\"FA-03057\">Protein kinase</family-name>\n" + 
        		"                            <family-name type=\"Family\" accession=\"FA-03117\">Tyr protein kinase</family-name>\n" + 
        		"                            <family-name type=\"Subfamily\" accession=\"FA-03128\">Insulin receptor</family-name>\n" + 
        		"                        </family-name-list>\n" + 
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
        		"                <chain-list>\n" + 
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
        		"                </chain-list>\n" + 
        		"                <gene-list>\n" + 
        		"                    <gene>\n" + 
        		"                        <gene-name type=\"primary\">INS</gene-name>\n" + 
        		"                    </gene>\n" + 
        		"                </gene-list>\n" + 
        		"                <family-list>\n" + 
        		"                    <family>\n" + 
        		"                        <family-name-list>\n" + 
        		"                            <family-name type=\"Family\" accession=\"FA-01869\">Insulin</family-name>\n" + 
        		"                        </family-name-list>\n" + 
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
        		"        Distributed under the Creative Commons Attribution-NoDerivs License - see http://creativecommons.org/licenses/by-nd/3.0/\n" + 
        		"    </copyright>\n" + 
        		"</nextprot-export>\n";

        XMLAssert.assertEquals(expectedXmlOutput, out.toString());
        //Assert.assertEquals(expectedXmlOutput, out.toString());
    }

    @Test
    public void testWriteXMLPrematureClose() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NPEntryXMLStreamWriter writer = new NPEntryXMLStreamWriter(out, "overview");
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
                "        Distributed under the Creative Commons Attribution-NoDerivs License - see http://creativecommons.org/licenses/by-nd/3.0/\n" +
                "    </copyright>\n" +
                "</nextprot-export>\n", out.toString());
    }
}