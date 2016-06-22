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
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeneVariantParserTest {

    @Test
    public void parserShouldExtractGeneNameAndProteinVariation() throws Exception {

        GeneVariantParser parser = new GeneVariantParser("SCN11A-p.Lys1710Thr",
                mockEntryIsoform(mockEntry("SCN11A", "SCN12A", "SNS2"),
                        Mockito.mock(Isoform.class)));

        Assert.assertEquals("SCN11A", parser.getGeneName());
        ProteinSequenceVariation mutation = parser.getProteinSequenceVariation();
        ProteinSequenceChange proteinSequenceChange2 = mutation.getProteinSequenceChange();

        Assert.assertEquals(AminoAcidCode.Lysine, mutation.getFirstChangingAminoAcid());
        Assert.assertEquals(1710, mutation.getFirstChangingAminoAcidPos());
        Assert.assertTrue(proteinSequenceChange2 instanceof Substitution);
    }

    @Test
    public void parserShouldExtractGeneNameAndProteinVariation2() throws Exception {

        GeneVariantParser parser = new GeneVariantParser("WT1-iso4-p.Phe154Ser",
                mockEntryIsoform(mockEntry("WT1"),
                        Mockito.mock(Isoform.class)));

        Assert.assertEquals("WT1", parser.getGeneName());
        ProteinSequenceVariation mutation = parser.getProteinSequenceVariation();
        ProteinSequenceChange proteinSequenceChange2 = mutation.getProteinSequenceChange();

        Assert.assertEquals(AminoAcidCode.Phenylalanine, mutation.getFirstChangingAminoAcid());
        Assert.assertEquals(154, mutation.getFirstChangingAminoAcidPos());
        Assert.assertTrue(proteinSequenceChange2 instanceof Substitution);
    }

    private EntryIsoform mockEntryIsoform(Entry entry, Isoform isoform) {

        EntryIsoform entryIsoform = Mockito.mock(EntryIsoform.class);
        when(entryIsoform.getEntry()).thenReturn(entry);
        when(entryIsoform.getIsoform()).thenReturn(isoform);
        when(entryIsoform.isCanonicalIsoform()).thenReturn(true);

        return entryIsoform;
    }

    private Isoform mockIsoform(String isoName, String name) {

        Isoform isoform = Mockito.mock(Isoform.class);
        when(isoform.getUniqueName()).thenReturn(isoName);

        EntityName entityName = mock(EntityName.class);
        when(entityName.getName()).thenReturn(name);

        when(isoform.getMainEntityName()).thenReturn(entityName);

        return isoform;
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