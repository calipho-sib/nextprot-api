package org.nextprot.api.isoform.mapper.domain.feature.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.UniProtPTM;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.isoform.mapper.IsoformMappingBaseTest;
import org.nextprot.api.isoform.mapper.domain.feature.impl.SequenceModification;
import org.nextprot.api.isoform.mapper.domain.feature.impl.SequenceVariant;

import java.text.ParseException;
import java.util.Arrays;

import static org.mockito.Mockito.when;

public class SequenceVariantTest extends IsoformMappingBaseTest {

    @Test
    public void shouldExtractGeneNameAndProteinVariation() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("SCN11A-p.Lys1710Thr");

        Assert.assertEquals("SCN11A", variant.getGeneName());
        SequenceVariation variation = variant.getProteinVariation();

        Assert.assertEquals(AminoAcidCode.LYSINE, variation.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1710, variation.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void shouldExtractGeneNameAndProteinVariation2() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1", variant.getGeneName());
        SequenceVariation variation = variant.getProteinVariation();

        Assert.assertEquals(AminoAcidCode.PHENYLALANINE, variation.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(154, variation.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertTrue(variant.isIsoformSpecific());
    }

    @Test
    public void testIsospecFeature() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("WT1-p.Phe154Ser");

        Isoform isoform = mockIsoform("NX_P19544", "Iso 1", true);

        Assert.assertEquals("WT1-iso1-p.Phe154Ser", variant.formatIsoSpecificFeature(isoform, 154, 154));
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testIsospecFeatureFromIsoFeature() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("WT1-iso4-p.Phe154Ser");

        Isoform isoform = mockIsoform("NX_P19544", "Iso 3", false);

        Assert.assertEquals("WT1-iso3-p.Phe120Ser", variant.formatIsoSpecificFeature(isoform, 120, 120));
        Assert.assertTrue(variant.isIsoformSpecific());
    }

    @Test
    public void testGetIsoformIso() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("SCN11A-iso2-p.Leu1158Pro");

        Assert.assertEquals("NX_Q9UI33-2", variant.getIsoform().getIsoformAccession());
        Assert.assertEquals("Iso 2", variant.getIsoform().getMainEntityName().getName());
        Assert.assertTrue(variant.isIsoformSpecific());
    }

    @Test
    public void testGetIsoformIsoCanonical() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("SCN11A-p.Leu1158Pro");

        Assert.assertEquals("NX_Q9UI33-1", variant.getIsoform().getIsoformAccession());
        Assert.assertEquals("Iso 1", variant.getIsoform().getMainEntityName().getName());
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testGetIsoformNonIso() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("INSR-isoshort-p.Arg113Pro");

        Assert.assertEquals("NX_P06213-2", variant.getIsoform().getIsoformAccession());
        Assert.assertEquals("Short", variant.getIsoform().getMainEntityName().getName());
        Assert.assertTrue(variant.isIsoformSpecific());
    }

    @Test
    public void testGetIsoformNonIso2() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("INSR-isoShort-p.Arg113Pro");

        Assert.assertEquals("NX_P06213-2", variant.getIsoform().getIsoformAccession());
        Assert.assertEquals("Short", variant.getIsoform().getMainEntityName().getName());
        Assert.assertTrue(variant.isIsoformSpecific());
    }

    @Test
    public void testGetIsoformCanonicalNonIsoType() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("INSR-p.Arg113Pro");

        Assert.assertEquals("NX_P06213-1", variant.getIsoform().getIsoformAccession());
        Assert.assertEquals("Long", variant.getIsoform().getMainEntityName().getName());
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testGetIsoformCaseInsensitive() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("ABL1-isoib-p.Ser439Gly");

        Assert.assertEquals("NX_P00519-2", variant.getIsoform().getIsoformAccession());
        Assert.assertTrue(variant.isIsoformSpecific());
    }

    @Test(expected = ParseException.class)
    public void shouldContainValidDashSeparator() throws Exception {

        SequenceVariant.variant("WT1:p.Phe154Ser");
    }

    @Test
    public void testFormatIsoformSpecifiqueFeatureTypeIso() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("SCN11A-p.Leu1158Pro");

        Isoform iso = mockIsoform("whatever", "Iso 1", true);

        Assert.assertEquals("iso1", variant.formatIsoformName(iso));
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testFormatIsoformSpecifiqueFeatureTypeNonIso() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("ABL1-p.Ser439Gly");

        Isoform iso = mockIsoform("whatever", "IA", true);

        Assert.assertEquals("isoIA", variant.formatIsoformName(iso));
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testFormatIsoformSpecifiqueFeatureTypeNonIsoWithSpace() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("GTF2A1-p.Gln13Thr");

        Isoform iso = mockIsoform("whatever", "37 kDa", true);

        Assert.assertEquals("iso37_kDa", variant.formatIsoformName(iso));
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testParseSequenceVariantWithPrefixSpace() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("    SCN11A-p.Leu1158Pro");
        Assert.assertEquals("SCN11A", variant.getGeneName());
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testParseSequenceVariantWithSuffixSpaces() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("SCN11A-p.Leu1158Pro          ");

        SequenceVariation variation = variant.getProteinVariation();

        Assert.assertEquals(AminoAcidCode.LEUCINE, variation.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1158, variation.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertFalse(variant.isIsoformSpecific());
    }

    @Test
    public void testParseIsoformSpecifiqueFeatureTypeNonIsoWithUnderscore() throws Exception {

        SequenceVariant variant = SequenceVariant.variant("GTF2A1-iso37_kDa-p.Gln13Thr");

        Assert.assertEquals("GTF2A1", variant.getGeneName());
        Assert.assertEquals("37 kDa", variant.getIsoform().getMainEntityName().getName());
        Assert.assertTrue(variant.isIsoformSpecific());
    }

    @Test
    public void testPTMWithCanonicalIsoform() throws Exception {

        SequenceModification ptm = new SequenceModification("NX_Q06187.PTM-0253_21");

        Assert.assertEquals("NX_Q06187-1", ptm.getIsoform().getIsoformAccession());
        SequenceVariation pv = ptm.getProteinVariation();
        Assert.assertEquals(AminoAcidCode.SERINE, pv.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(21, pv.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.SERINE, pv.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(21, pv.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertTrue(pv.getSequenceChange() instanceof UniProtPTM);
        Assert.assertEquals("PTM-0253", ((UniProtPTM)pv.getSequenceChange()).getValue());
        Assert.assertFalse(ptm.isIsoformSpecific());
    }

    @Test
    public void testPTMWithSpecifiedIsoform() throws Exception {

        SequenceModification ptm = new SequenceModification("NX_Q06187-2.PTM-0253_21");
        SequenceVariation variation = ptm.getProteinVariation();
        SequenceChange<?> seqChange = variation.getSequenceChange();
        Assert.assertTrue(seqChange instanceof UniProtPTM);
        Assert.assertEquals("PTM-0253", ((UniProtPTM)seqChange).getValue());
        Assert.assertEquals("NX_Q06187-2", ptm.getIsoform().getIsoformAccession());
        Assert.assertTrue(ptm.isIsoformSpecific());
    }

    @Test
    public void testFormatPTM() throws Exception {

        SequenceModification ptm = new SequenceModification("NX_P10070.PTM-0253_388");
        SequenceVariation variation = ptm.getProteinVariation();
        SequenceChange<?> seqChange = variation.getSequenceChange();
        Assert.assertTrue(seqChange instanceof UniProtPTM);
        Assert.assertEquals("PTM-0253", ((UniProtPTM)seqChange).getValue());
        Assert.assertEquals("NX_P10070-5", ptm.getIsoform().getIsoformAccession());
        Assert.assertFalse(ptm.isIsoformSpecific());

        Isoform isoform = mockIsoform("NX_P10070-1", "Iso 1", false);

        Assert.assertEquals("NX_P10070-1.PTM-0253_60", ptm.formatIsoSpecificFeature(isoform, 60, 60));
    }

    public static Entry mockEntry(String accession, Isoform... isoforms) {

        Entry entry = Mockito.mock(Entry.class);

        when(entry.getUniqueName()).thenReturn(accession);
        when(entry.getIsoforms()).thenReturn(Arrays.asList(isoforms));

        return entry;
    }

    public static Isoform mockIsoform(String accession, String name, boolean canonical) {

        Isoform isoform = Mockito.mock(Isoform.class);
        when(isoform.getIsoformAccession()).thenReturn(accession);
        when(isoform.isCanonicalIsoform()).thenReturn(canonical);

        EntityName entityName = Mockito.mock(EntityName.class);
        when(entityName.getName()).thenReturn(name);

        when(isoform.getMainEntityName()).thenReturn(entityName);

        return isoform;
    }

    public static Isoform mockIsoform(String accession, String name, boolean canonical, String sequence) {

        Isoform isoform = mockIsoform(accession, name, canonical);
        when(isoform.getSequence()).thenReturn(sequence);

        return isoform;
    }
}