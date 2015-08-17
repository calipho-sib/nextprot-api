package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 17/08/15.
 */
public class XMLPrettyPrinterTest {

    @Test
    public void testPrettyfying() throws Exception {

        XMLPrettyPrinter printer = new XMLPrettyPrinter();

        String xml = newMessyXml();

        Assert.assertEquals("<overview>\n" +
                "    <protein-existence value=\"Evidence_at_protein_level\"/>\n" +
                "    <protein-name-list>\n" +
                "        <recommended-name>\n" +
                "            <protein-name qualifier=\"full\">Lysozyme-like protein 6</protein-name>\n" +
                "            <protein-name qualifier=\"EC\">3.2.1.17</protein-name>\n" +
                "        </recommended-name>\n" +
                "        <alternative-name-list/>\n" +
                "    </protein-name-list>\n" +
                "    <gene-list>\n" +
                "        <gene>\n" +
                "            <gene-name type=\"primary\">LYZL6</gene-name>\n" +
                "            <gene-name type=\"synonym\">LYC1</gene-name>\n" +
                "            <gene-name type=\"ORF\">UNQ754/PRO1485</gene-name>\n" +
                "        </gene>\n" +
                "    </gene-list>\n" +
                "    <family-list>\n" +
                "        <family>\n" +
                "            <family-name-list>\n" +
                "                <family-name type=\"Family\" accession=\"FA-01575\">Glycosyl hydrolase 22</family-name>\n" +
                "            </family-name-list>\n" +
                "        </family>\n" +
                "    </family-list>\n" +
                "    <history>\n" +
                "        <entry-history database=\"neXtProt\" integrated=\"2010-03-01\" updated=\"2015-05-04\"/>\n" +
                "        <entry-history database=\"UniProtKB\" integrated=\"2006-06-27\" updated=\"2015-04-29\" version=\"114\" last-sequence-update=\"1998-11-01\" sequence-version=\"1\"/>\n" +
                "    </history>\n" +
                "</overview>\n", printer.prettify(xml));
    }

    @Test
    public void testPrettyfyingWithLevel() throws Exception {

        XMLPrettyPrinter printer = new XMLPrettyPrinter();

        String xml = newMessyXml();

        Assert.assertEquals("    <overview>\n" +
                "        <protein-existence value=\"Evidence_at_protein_level\"/>\n" +
                "        <protein-name-list>\n" +
                "            <recommended-name>\n" +
                "                <protein-name qualifier=\"full\">Lysozyme-like protein 6</protein-name>\n" +
                "                <protein-name qualifier=\"EC\">3.2.1.17</protein-name>\n" +
                "            </recommended-name>\n" +
                "            <alternative-name-list/>\n" +
                "        </protein-name-list>\n" +
                "        <gene-list>\n" +
                "            <gene>\n" +
                "                <gene-name type=\"primary\">LYZL6</gene-name>\n" +
                "                <gene-name type=\"synonym\">LYC1</gene-name>\n" +
                "                <gene-name type=\"ORF\">UNQ754/PRO1485</gene-name>\n" +
                "            </gene>\n" +
                "        </gene-list>\n" +
                "        <family-list>\n" +
                "            <family>\n" +
                "                <family-name-list>\n" +
                "                    <family-name type=\"Family\" accession=\"FA-01575\">Glycosyl hydrolase 22</family-name>\n" +
                "                </family-name-list>\n" +
                "            </family>\n" +
                "        </family-list>\n" +
                "        <history>\n" +
                "            <entry-history database=\"neXtProt\" integrated=\"2010-03-01\" updated=\"2015-05-04\"/>\n" +
                "            <entry-history database=\"UniProtKB\" integrated=\"2006-06-27\" updated=\"2015-04-29\" version=\"114\" last-sequence-update=\"1998-11-01\" sequence-version=\"1\"/>\n" +
                "        </history>\n" +
                "    </overview>\n", printer.prettify(xml, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrettyfyingWithBadLevel() throws Exception {

        XMLPrettyPrinter printer = new XMLPrettyPrinter();
        printer.prettify(newMessyXml(), -1);
    }


    private String newMessyXml() {

        return "<overview>\n" +
                "    <protein-existence value='Evidence_at_protein_level'/>\n" +
                "    <protein-name-list>\n" +
                "        <recommended-name>\n" +
                "                <protein-name qualifier=\"full\">Lysozyme-like protein 6</protein-name>\n" +
                "\t\t    \t        <protein-name qualifier=\"EC\">3.2.1.17</protein-name>\n" +
                "\t    \t        </recommended-name>\n" +
                "        <alternative-name-list>\n" +
                "            </alternative-name-list>\n" +
                "    </protein-name-list>\n" +
                "\n" +
                "\n" +
                "\n" +
                "    <gene-list>\n" +
                "            <gene>\n" +
                "            <gene-name type=\"primary\">LYZL6</gene-name>\n" +
                "                        \t\t        <gene-name type=\"synonym\">LYC1</gene-name>\n" +
                "                                                    <gene-name type=\"ORF\">UNQ754/PRO1485</gene-name>\n" +
                "\t\t                        </gene>\n" +
                "        </gene-list>\n" +
                "    <family-list>\n" +
                "                    \n" +
                "\n" +
                " \n" +
                "\n" +
                "\n" +
                "<family>\n" +
                "\t<family-name-list>\n" +
                "\t\t<family-name type=\"Family\" accession=\"FA-01575\">Glycosyl hydrolase 22</family-name>\n" +
                "\t</family-name-list>\n" +
                "</family>\n" +
                "\n" +
                "            </family-list>\n" +
                "    <history>\n" +
                "            <entry-history database=\"neXtProt\" integrated=\"2010-03-01\" updated=\"2015-05-04\"/>\n" +
                "                <entry-history database=\"UniProtKB\" integrated=\"2006-06-27\" updated=\"2015-04-29\" version=\"114\" last-sequence-update=\"1998-11-01\" sequence-version=\"1\"/>\n" +
                "        </history>\n" +
                "</overview>";
    }
}