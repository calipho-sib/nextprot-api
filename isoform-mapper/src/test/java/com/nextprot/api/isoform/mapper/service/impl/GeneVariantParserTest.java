package com.nextprot.api.isoform.mapper.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.mutation.Mutation;
import org.nextprot.api.commons.bio.mutation.ProteinMutation;
import org.nextprot.api.commons.bio.mutation.Substitution;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.OverviewService;

public class GeneVariantParserTest {

    @Test
    public void parserShouldExtractGeneNameAndProteinMutation() throws Exception {

        GeneVariantParser parser = new GeneVariantParser("SCN11A-p.Lys1710Thr", "NX_Q9UI33", mockService("SCN11A"));
        Assert.assertEquals("SCN11A", parser.getGeneName());
        ProteinMutation mutation = parser.getProteinMutation();
        Mutation mutation2 = mutation.getMutation();

        Assert.assertEquals(AminoAcidCode.Lysine, mutation.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(1710, mutation.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(mutation2 instanceof Substitution);
    }

    private OverviewService mockService(String mainGeneName) {

        OverviewService service = Mockito.mock(OverviewService.class);
        Overview overview = Mockito.mock(Overview.class);
        Mockito.when(overview.getMainGeneName()).thenReturn(mainGeneName);
        Mockito.when(service.findOverviewByEntry("NX_Q9UI33")).thenReturn(overview);

        return service;
    }

}