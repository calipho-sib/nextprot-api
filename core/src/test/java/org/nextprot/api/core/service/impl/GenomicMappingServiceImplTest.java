package org.nextprot.api.core.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformMapping;
import org.nextprot.api.core.domain.TranscriptMapping;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

@ActiveProfiles({ "dev" })
public class GenomicMappingServiceImplTest extends CoreUnitBaseTest {

    @Autowired
    private GenomicMappingService genomicMappingService;

    @Test
    public void NX_P78324_4MappingAgainstNX_ENST00000356025ShouldHaveValidExons() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_P78324");
        Assert.assertEquals(1, gml.size());

        List<IsoformMapping> iml = gml.get(0).getIsoformMappings();
        Assert.assertEquals(3, iml.size());

        assertExonStructures(iml, "NX_P78324-4", "NX_ENSG00000198053", "NX_ENST00000356025",
                "1282-1360,20933-21240,21244-21289,27229-27546,28147-28479,30598-30711,33709-33733,40549-40588,43154-43399",
                "617-630,1273-1360,20933-21240,21244-21289,27229-27546,28147-28479,30598-30711,33709-33733,40549-40588,43154-45731");
    }

    @Test
    public void NX_Q12805_3MappingAgainstNX_ENST00000355426ShouldHaveValidExons() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_Q12805");
        Assert.assertEquals(1, gml.size());

        List<IsoformMapping> iml = gml.get(0).getIsoformMappings();
        Assert.assertEquals(5, iml.size());

        assertExonStructures(iml, "NX_Q12805-3", "NX_ENSG00000115380", "NX_ENST00000355426",
                "1700-1780,5873-5921,6089-6126,6130-6475,42406-42528,46275-46394,47398-47517,49075-49194,53017-53140,53225-53420,56906-57064",
                "353-429,1201-1241,1693-1780,5873-5921,6089-6126,6130-6475,42406-42528,46275-46394,47398-47517,49075-49194,53017-53140,53225-53420,56906-57280");
    }

    private void assertExonStructures(List<IsoformMapping> iml, String isoName, String expEnsg, String expEnst, String expIsoPosOnRefGene, String expExonPosOnRefGene) {

        IsoformMapping isoformMapping = iml.stream()
                .filter(im -> im.getUniqueName().equals(isoName))
                .collect(Collectors.toList()).get(0);

        Assert.assertEquals(expEnsg, isoformMapping.getReferenceGeneName());

        String isoPosOnRefGene = isoformMapping.getPositionsOfIsoformOnReferencedGene().stream()
                .map(k -> k.getKey()+"-"+k.getValue())
                .collect(Collectors.joining(","));

        Assert.assertEquals(expIsoPosOnRefGene, isoPosOnRefGene);

        TranscriptMapping transcriptMapping = isoformMapping.getTranscriptMappings().stream()
                .filter(tm -> tm.getUniqueName().equals(expEnst))
                .collect(Collectors.toList()).get(0);

        String exonPosOnRefGene = transcriptMapping.getExons().stream()
                .map(e -> e.getFirstPositionOnGene()+"-"+e.getLastPositionOnGene())
                .collect(Collectors.joining(","));

        Assert.assertEquals(expExonPosOnRefGene, exonPosOnRefGene);

    }
}