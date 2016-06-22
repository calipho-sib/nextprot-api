package com.nextprot.api.isoform.mapper.utils;

import com.nextprot.api.isoform.mapper.utils.GeneVariantParser;
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

public class GeneVariantParserTest {

    @Test
    public void parserShouldExtractGeneNameAndProteinMutation() throws Exception {

        GeneVariantParser parser = new GeneVariantParser("SCN11A-p.Lys1710Thr", mockEntry("SCN11A", "SCN12A", "SNS2"));
        Assert.assertEquals("SCN11A", parser.getGeneName());
        ProteinSequenceVariation mutation = parser.getProteinSequenceVariation();
        ProteinSequenceChange proteinSequenceChange2 = mutation.getProteinSequenceChange();

        Assert.assertEquals(AminoAcidCode.Lysine, mutation.getFirstChangingAminoAcid());
        Assert.assertEquals(1710, mutation.getFirstChangingAminoAcidPos());
        Assert.assertTrue(proteinSequenceChange2 instanceof Substitution);
    }

    private Entry mockEntry(String... geneNames) {

        Entry entry = Mockito.mock(Entry.class);
        Overview overview = Mockito.mock(Overview.class);
        Mockito.when(entry.getOverview()).thenReturn(overview);

        List<EntityName> names = new ArrayList<>();
        for (String geneName : geneNames) {
            EntityName entityName = new EntityName();
            entityName.setName(geneName);
            names.add(entityName);
        }

        Mockito.when(overview.getGeneNames()).thenReturn(names);

        return entry;
    }

}