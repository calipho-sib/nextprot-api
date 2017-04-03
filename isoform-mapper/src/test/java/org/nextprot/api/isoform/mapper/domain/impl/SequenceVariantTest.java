package org.nextprot.api.isoform.mapper.domain.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.nextprot.api.core.domain.EntryUtilsTest.mockEntry;
import static org.nextprot.api.core.domain.EntryUtilsTest.mockIsoform;

public class SequenceVariantTest {

    @Test
    public void shouldExtractGeneNameAndProteinVariation() throws Exception {

        SequenceVariant variant = new SequenceVariant("SCN11A-p.Lys1710Thr");

        Assert.assertEquals("SCN11A", variant.getGeneName());
        SequenceVariation variation = variant.getProteinVariation();
        Assert.assertEquals("p.Lys1710Thr", variant.getFormattedVariation());

        Assert.assertEquals(AminoAcidCode.LYSINE, variation.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1710, variation.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertTrue(variant.isValidGeneName(mockEntryWithGenes("SCN11A", "SCN12A", "SNS2")));
    }

    @Test
    public void shouldExtractGeneNameAndProteinVariation2() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1", variant.getGeneName());
        SequenceVariation variation = variant.getProteinVariation();
        Assert.assertEquals("p.Phe154Ser", variant.getFormattedVariation());

        Assert.assertEquals(AminoAcidCode.PHENYLALANINE, variation.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(154, variation.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertTrue(variant.isValidGeneName(mockEntryWithGenes("WT1")));
    }

    @Test
    public void testIsospecFeature() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-p.Phe154Ser");

        Isoform isoform = mockIsoform("NX_P19544", "Iso 1", true);

        Assert.assertEquals("WT1-iso1-p.Phe154Ser", variant.formatIsoSpecificFeature(isoform, 154, 154));
    }

    @Test
    public void testIsospecFeatureFromIsoFeature() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-iso4-p.Phe154Ser");

        Isoform isoform = mockIsoform("NX_P19544", "Iso 3", false);

        Assert.assertEquals("WT1-iso3-p.Phe120Ser", variant.formatIsoSpecificFeature(isoform, 120, 120));
    }

    @Test
    public void testGetIsoformIso() throws Exception {

        SequenceVariant variant = new SequenceVariant("SCN11A-iso2-p.Leu1158Pro");

        Entry entry = mockEntry("NX_P06213",
                mockIsoform("NX_Q9UI33-1", "Iso 1", true),
                mockIsoform("NX_Q9UI33-2", "Iso 2", false),
                mockIsoform("NX_Q9UI33-3", "Iso 3", false));

        Assert.assertEquals("NX_Q9UI33-2", variant.getIsoform(entry).getUniqueName());
        Assert.assertEquals("Iso 2", variant.getIsoformName());
    }

    @Test
    public void testGetIsoformIsoCanonical() throws Exception {

        SequenceVariant variant = new SequenceVariant("SCN11A-p.Leu1158Pro");

        Entry entry = mockEntry("NX_P06213",
                mockIsoform("NX_Q9UI33-1", "Iso 1", true),
                mockIsoform("NX_Q9UI33-2", "Iso 2", false),
                mockIsoform("NX_Q9UI33-3", "Iso 3", false));

        Assert.assertEquals("NX_Q9UI33-1", variant.getIsoform(entry).getUniqueName());
        Assert.assertNull(variant.getIsoformName());
    }

    @Test
    public void testGetIsoformNonIso() throws Exception {

        SequenceVariant variant = new SequenceVariant("INSR-isoshort-p.Arg113Pro");

        Entry entry = mockEntry("NX_P06213",
                mockIsoform("NX_P06213-1", "Long", true),
                mockIsoform("NX_P06213-2", "Short", false));

        Assert.assertEquals("NX_P06213-2", variant.getIsoform(entry).getUniqueName());
        Assert.assertEquals("short", variant.getIsoformName());
    }

    @Test
    public void testGetIsoformNonIso2() throws Exception {

        SequenceVariant variant = new SequenceVariant("INSR-isoShort-p.Arg113Pro");

        Entry entry = mockEntry("NX_P06213",
                mockIsoform("NX_P06213-1", "Long", true),
                mockIsoform("NX_P06213-2", "Short", false));

        Assert.assertEquals("NX_P06213-2", variant.getIsoform(entry).getUniqueName());
        Assert.assertEquals("Short", variant.getIsoformName());
    }

    @Test
    public void testGetIsoformNonIsoCanonical() throws Exception {

        SequenceVariant variant = new SequenceVariant("INSR-p.Arg113Pro");

        Entry entry = mockEntry("NX_P06213",
                mockIsoform("NX_P06213-1", "Long", true),
                mockIsoform("NX_P06213-2", "Short", false));

        Assert.assertEquals("NX_P06213-1", variant.getIsoform(entry).getUniqueName());
        Assert.assertNull(variant.getIsoformName());
    }

    @Test
    public void testGetIsoformCaseInsensitive() throws Exception {

        SequenceVariant variant = new SequenceVariant("ABL1-isoib-p.Ser439Gly");

        Entry entry = mockEntry("NX_P00519",
                mockIsoform("NX_P00519-1", "IA", true),
                mockIsoform("NX_P00519-2", "IB", false));

        Assert.assertEquals("NX_P00519-2", variant.getIsoform(entry).getUniqueName());
    }

    @Test(expected = ParseException.class)
    public void shouldContainValidDashSeparator() throws Exception {

        new SequenceVariant("WT1:p.Phe154Ser");
    }

    @Test
    public void testFormatIsoformSpecifiqueFeatureTypeIso() throws Exception {

        SequenceVariant variant = new SequenceVariant("SCN11A-p.Leu1158Pro");

        Isoform iso = mockIsoform("whatever", "Iso 1", true);

        Assert.assertEquals("iso1", variant.formatIsoformFeatureName(iso));
    }

    @Test
    public void testFormatIsoformSpecifiqueFeatureTypeNonIso() throws Exception {

        SequenceVariant variant = new SequenceVariant("ABL1-p.Ser439Gly");

        Isoform iso = mockIsoform("whatever", "IA", true);

        Assert.assertEquals("isoIA", variant.formatIsoformFeatureName(iso));
    }

    @Test
    public void testFormatIsoformSpecifiqueFeatureTypeNonIsoWithSpace() throws Exception {

        SequenceVariant variant = new SequenceVariant("GTF2A1-p.Gln13Thr");

        Isoform iso = mockIsoform("whatever", "37 kDa", true);

        Assert.assertEquals("iso37_kDa", variant.formatIsoformFeatureName(iso));
    }

    @Test
    public void testParseSequenceVariantWithPrefixSpace() throws Exception {

        SequenceVariant variant = new SequenceVariant("    SCN11A-p.Leu1158Pro");

        Assert.assertEquals("SCN11A", variant.getGeneName());
    }

    @Test
    public void testParseSequenceVariantWithSuffixSpaces() throws Exception {

        SequenceVariant variant = new SequenceVariant("SCN11A-p.Leu1158Pro          ");

        Assert.assertEquals("p.Leu1158Pro", variant.getFormattedVariation());
    }

    @Test
    public void testParseIsoformSpecifiqueFeatureTypeNonIsoWithUnderscore() throws Exception {

        SequenceVariant variant = new SequenceVariant("GTF2A1-iso37_kDa-p.Gln13Thr");

        Assert.assertEquals("GTF2A1", variant.getGeneName());
        Assert.assertEquals("p.Gln13Thr", variant.getFormattedVariation());
        Assert.assertEquals("37 kDa", variant.getIsoformName());

        Assert.assertTrue(variant.isValidGeneName(mockEntryWithGenes("GTF2A1", "TF2A1")));
    }

    private Entry mockEntryWithGenes(String... geneNames) {

        Entry entry = Mockito.mock(Entry.class);
        Overview overview = Mockito.mock(Overview.class);
        when(entry.getOverview()).thenReturn(overview);

        List<EntityName> names = new ArrayList<>();
        for (String geneName : geneNames) {
            EntityName entityName = new EntityName();
            entityName.setName(geneName);
            names.add(entityName);
        }

        when(overview.getGeneNames()).thenReturn(names);

        return entry;
    }

    /** Other problematic entries:
     *
     *  NX_Q9BX84-7 M6-kinase 3
     *  NX_O95704-3 III
     *  NX_P29590-12 PML-12
     */

}