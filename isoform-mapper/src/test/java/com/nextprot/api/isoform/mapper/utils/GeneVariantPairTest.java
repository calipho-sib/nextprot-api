package com.nextprot.api.isoform.mapper.utils;

import com.nextprot.api.isoform.mapper.domain.IsoformFeature;
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

public class GeneVariantPairTest {

    @Test
    public void shouldExtractGeneNameAndProteinVariation() throws Exception {

        GeneVariantPair pair = new GeneVariantPair("SCN11A-p.Lys1710Thr");

        Assert.assertEquals("SCN11A", pair.getGeneName());
        IsoformFeature isoformFeature = pair.getFeature();

        Assert.assertEquals(AminoAcidCode.Lysine, isoformFeature.getFirstChangingAminoAcid());
        Assert.assertEquals(1710, isoformFeature.getFirstChangingAminoAcidPos());

        Assert.assertTrue(pair.isValidGeneName(mockEntry("SCN11A", "SCN12A", "SNS2")));
    }

    @Test
    public void shouldExtractGeneNameAndProteinVariation2() throws Exception {

        GeneVariantPair pair = new GeneVariantPair("WT1-iso4-p.Phe154Ser");

        Assert.assertEquals("WT1", pair.getGeneName());
        IsoformFeature isoformFeature = pair.getFeature();

        Assert.assertEquals(AminoAcidCode.Phenylalanine, isoformFeature.getFirstChangingAminoAcid());
        Assert.assertEquals(154, isoformFeature.getFirstChangingAminoAcidPos());

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