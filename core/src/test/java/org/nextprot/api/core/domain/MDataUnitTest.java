package org.nextprot.api.core.domain;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MDataUnitTest {

    @Test
    public void testWithMultiplePublications() {
        String xml = "<metadata><DM label=\"Detection method\" isVariable=\"N\" >Mass spectrometry LC-MS/MS.</DM>\n<DC label=\"Data confidence documentation\" isVariable=\"N\" >GOLD: All peptides.</DC>\n</metadata><publications>\n<publication key=\"1\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"22068332\"></db_xref></publication>\n<publication key=\"2\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"22278370\"></db_xref></publication>\n<publication key=\"3\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"23242552\"></db_xref></publication>\n<publication key=\"4\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"23933261\"></db_xref></publication>\n<publication key=\"5\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"24870543\"></db_xref></publication>\n<publication key=\"6\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"25977788\"></db_xref></publication>\n<publication key=\"7\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"28112733\"></db_xref></publication>\n</publications>";
        Mdata.MDataContext mc = Mdata.convertXmlToMDataContext(xml);
        assertEquals(mc.getPublications().getPublication().size(), 7);
    }

    @Test
    public void testMdata() {
        String xml = "<metadata><DM label=\"Detection method\" isVariable=\"N\" >Mass spectrometry Nano LC-MS/MS.</DM>\n" +
                "<CL label=\"Cell line\" isVariable=\"N\" >Jurkat E6.1 [CVCL_0367].</CL>\n" +
                "<SP label=\"Sample preparation\" isVariable=\"N\" >Lysate denaturation and treatment with either proteasome inhibitor MG132 or deubiquitinase inhibitor PR-619. Lysate in-solution digestion with trypsin. Peptides desaltification and off-line basic RP fractionation. Enrichment of Lys ubiquitinated sites by immunoprecipitation with anti-Lys-epsilon-GlyGly antibody followed by desaltification.</SP>\n" +
                "<IP label=\"Instrument/platform\" isVariable=\"N\" >Nanoscale C18 HPLC coupled to Q Exactive mass spectrometer equipped with a nanoelectrospray source (Thermo Scientific).</IP>\n" +
                "<DA label=\"Data analysis procedure\" isVariable=\"N\" >Protein database: UniProtKB/SwissProt (release number not available). Software: MaxQuant version 1.3.0.5. Maximum missed cleavages: 2. Mass tolerance for parent ion: 6 ppm. Mass tolerance for fragment ion: Not available. Fixed modification: Cys carbamidomethylation. Variable modifications: Met oxidation; Gly-Gly addition to Lys; N-terminal protein acetylation. Peptide FDR &lt; 0.01. Protein FDR &lt; 0.01. Minimum peptide length: 6. Ion mode: Positive. Spectra acquisition: 12 most intense precursor ions. Lys-epsilon-GlyGly sites localized to a peptide C-terminal Lys residue were removed. Spectra recalibration: Not available</DA>\n" +
                "<DP label=\"Data processing by neXtProt\" >Exclusion of peptides of less than 7 amino acids. Alignment of peptides on the human proteome for exact matches. Peptides matching more than one entry are labeled 'found in other entries'. Exclusion of modifications found on non-proteotypic peptides. GLOBAL. Ubiquitination: a small fraction of ubiquitinated sites is likely to originate from modification by ISG15 or NEDD8 and is filtered according to neXtProt sequence annotation.</DP>\n" +
                "<DC label=\"Data confidence documentation\" isVariable=\"N\" >Peptide identification. GOLD: Andromeda score &gt;= 100. LOCAL. Ubiquitination. GOLD: Andromeda score &gt;= 100; PTM localization probability &gt;= 0.99. SILVER: Andromeda score &gt;= 100; PTM localization probability &gt;= 0.95. N-terminal protein acetylation: GOLD.</DC>\n" +
                "</metadata><publications>\n" +
                "<publication key=\"1\" type=\"ARTICLE\"><db_xref db=\"PubMed\" dbkey=\"23266961\"></db_xref></publication>\n" +
                "</publications>";

        Mdata.MDataContext mc = Mdata.convertXmlToMDataContext(xml);
        assertEquals(mc.getMetadata().getCL().getContent(), "Jurkat E6.1 [CVCL_0367].");
        assertEquals(mc.getMetadata().getCL().getIsVariable(), "N");
        assertEquals(mc.getMetadata().getDA().getLabel(), "Data analysis procedure");
        assertEquals(mc.getPublications().getPublication().get(0).getDb_xref().getDbkey(), "23266961");

    }

}
