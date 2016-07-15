package com.nextprot.api.isoform.mapper.domain.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class SequenceVariantTest {

    @Test
    public void shouldExtractGeneNameAndProteinVariation() throws Exception {

        SequenceVariant variant = new SequenceVariant("SCN11A-p.Lys1710Thr");

        Assert.assertEquals("SCN11A", variant.getGeneName());
        SequenceVariation variation = variant.getVariation();
        Assert.assertEquals("p.Lys1710Thr", variant.getFormattedVariation());

        Assert.assertEquals(AminoAcidCode.LYSINE, variation.getFirstChangingAminoAcid());
        Assert.assertEquals(1710, variation.getFirstChangingAminoAcidPos());

        Assert.assertTrue(variant.isValidGeneName(mockEntry("SCN11A", "SCN12A", "SNS2")));
    }

    @Test
    public void shouldExtractGeneNameAndProteinVariation2() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1", variant.getGeneName());
        SequenceVariation variation = variant.getVariation();
        Assert.assertEquals("p.Phe154Ser", variant.getFormattedVariation());

        Assert.assertEquals(AminoAcidCode.PHENYLALANINE, variation.getFirstChangingAminoAcid());
        Assert.assertEquals(154, variation.getFirstChangingAminoAcidPos());

        Assert.assertTrue(variant.isValidGeneName(mockEntry("WT1")));
    }

    @Test
    public void testIsospecFeature() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-p.Phe154Ser");

        Assert.assertEquals("WT1-iso1-p.Phe154Ser", variant.formatIsoSpecificFeature(1, 154, 154));
    }

    @Test
    public void testIsospecFeatureFromIsoFeature() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1-iso3-p.Phe120Ser", variant.formatIsoSpecificFeature(3, 120, 120));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testIsospecBadIsonumber() throws Exception {

        SequenceVariant variant = new SequenceVariant("WT1-iso4-p.Phe154Ser");
        variant.formatIsoSpecificFeature(0, 154, 154);
    }

    private Entry mockEntry(String... geneNames) {

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