package com.nextprot.api.isoform.mapper.domain.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;

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

        Assert.assertEquals(AminoAcidCode.LYSINE, variation.getFirstChangingAminoAcid());
        Assert.assertEquals(1710, variation.getFirstChangingAminoAcidPos());

        Assert.assertTrue(variant.isValidGeneName(mockEntryWithGenes("SCN11A", "SCN12A", "SNS2")));
    }

    @Test
    public void shouldExtractGeneNameAndProteinVariation2() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1", variant.getGeneName());
        SequenceVariation variation = variant.getProteinVariation();
        Assert.assertEquals("p.Phe154Ser", variant.getFormattedVariation());

        Assert.assertEquals(AminoAcidCode.PHENYLALANINE, variation.getFirstChangingAminoAcid());
        Assert.assertEquals(154, variation.getFirstChangingAminoAcidPos());

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

}