package com.nextprot.api.isoform.mapper.domain.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class SequenceVariationTest {

    @Test
    public void shouldExtractGeneNameAndProteinVariation() throws Exception {

        SequenceVariation pair = new SequenceVariation("SCN11A-p.Lys1710Thr");

        Assert.assertEquals("SCN11A", pair.getGeneName());
        org.nextprot.api.commons.bio.variation.SequenceVariation variation = pair.getVariation();

        Assert.assertEquals(AminoAcidCode.LYSINE, variation.getFirstChangingAminoAcid());
        Assert.assertEquals(1710, variation.getFirstChangingAminoAcidPos());

        Assert.assertTrue(pair.isValidGeneName(mockEntry("SCN11A", "SCN12A", "SNS2")));
    }

    @Test
    public void shouldExtractGeneNameAndProteinVariation2() throws Exception {

        SequenceVariation pair = new SequenceVariation("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1", pair.getGeneName());
        org.nextprot.api.commons.bio.variation.SequenceVariation variation = pair.getVariation();

        Assert.assertEquals(AminoAcidCode.PHENYLALANINE, variation.getFirstChangingAminoAcid());
        Assert.assertEquals(154, variation.getFirstChangingAminoAcidPos());

        Assert.assertTrue(pair.isValidGeneName(mockEntry("WT1")));
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