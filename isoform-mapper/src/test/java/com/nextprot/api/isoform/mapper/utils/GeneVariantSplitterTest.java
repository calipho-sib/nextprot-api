package com.nextprot.api.isoform.mapper.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ProteinSequenceChange;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.Substitution;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class GeneVariantSplitterTest {

    @Test
    public void shouldExtractGeneNameAndProteinVariation() throws Exception {

        GeneVariantSplitter parser = new GeneVariantSplitter("SCN11A-p.Lys1710Thr");

        Assert.assertEquals("SCN11A", parser.getGeneName());
        ProteinSequenceVariation mutation = parser.getVariant();
        ProteinSequenceChange proteinSequenceChange2 = mutation.getProteinSequenceChange();

        Assert.assertEquals(AminoAcidCode.Lysine, mutation.getFirstChangingAminoAcid());
        Assert.assertEquals(1710, mutation.getFirstChangingAminoAcidPos());
        Assert.assertTrue(proteinSequenceChange2 instanceof Substitution);

        Assert.assertTrue(parser.isValidGeneName(mockEntry("SCN11A", "SCN12A", "SNS2")));
    }

    @Test
    public void shouldExtractGeneNameAndProteinVariation2() throws Exception {

        GeneVariantSplitter parser = new GeneVariantSplitter("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1", parser.getGeneName());
        ProteinSequenceVariation mutation = parser.getVariant();
        ProteinSequenceChange proteinSequenceChange2 = mutation.getProteinSequenceChange();

        Assert.assertEquals(AminoAcidCode.Phenylalanine, mutation.getFirstChangingAminoAcid());
        Assert.assertEquals(154, mutation.getFirstChangingAminoAcidPos());
        Assert.assertTrue(proteinSequenceChange2 instanceof Substitution);

        Assert.assertTrue(parser.isValidGeneName(mockEntry("WT1")));
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