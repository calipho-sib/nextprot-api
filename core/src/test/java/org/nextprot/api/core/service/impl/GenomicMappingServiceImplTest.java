package org.nextprot.api.core.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.EntryExonMappingService;
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

    @Autowired
    private EntryExonMappingService entryExonMappingService;

    @Test
    public void NX_P78324_4MappingAgainstNX_ENST00000356025ShouldHaveValidExons() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_P78324");
        Assert.assertEquals(1, gml.size());

        List<IsoformGeneMapping> iml = gml.get(0).getIsoformGeneMappings();
        Assert.assertEquals(3, iml.size());

        assertExonStructures(iml, "NX_P78324-4", "NX_ENSG00000198053", "NX_ENST00000356025",
                "1282-1360,20933-21240,21244-21289,27229-27546,28147-28479,30598-30711,33709-33733,40549-40588,43154-43399",
                "617-630,1273-1360,20933-21240,21244-21289,27229-27546,28147-28479,30598-30711,33709-33733,40549-40588,43154-45731");
    }

    @Test
    public void NX_Q12805_3MappingAgainstNX_ENST00000355426ShouldHaveValidExons() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_Q12805");
        Assert.assertEquals(1, gml.size());

        List<IsoformGeneMapping> iml = gml.get(0).getIsoformGeneMappings();
        Assert.assertEquals(5, iml.size());

        assertExonStructures(iml, "NX_Q12805-3", "NX_ENSG00000115380", "NX_ENST00000355426",
                "1700-1780,5873-5921,6089-6126,6130-6475,42406-42528,46275-46394,47398-47517,49075-49194,53017-53140,53225-53420,56906-57064",
                "353-429,1201-1241,1693-1780,5873-5921,6089-6126,6130-6475,42406-42528,46275-46394,47398-47517,49075-49194,53017-53140,53225-53420,56906-57280");
    }

    @Test
    public void testIsoformAccessionAndName() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_P78324");

        Assert.assertEquals("NX_P78324-1", gml.get(0).getIsoformGeneMappings().get(0).getIsoformAccession());
        Assert.assertEquals("Iso 1", gml.get(0).getIsoformGeneMappings().get(0).getIsoformMainName());
    }

    @Test
    public void testIsoformNX_Q9H221_2SilverExonMapping() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_Q9H221");

        Assert.assertEquals(2, gml.get(0).getIsoformGeneMappings().size());
        Assert.assertEquals("Iso 1", gml.get(0).getIsoformGeneMappings().get(0).getIsoformMainName());
        Assert.assertEquals("GOLD", gml.get(0).getIsoformGeneMappings().get(0).getQuality());
        Assert.assertEquals("Iso 2", gml.get(0).getIsoformGeneMappings().get(1).getIsoformMainName());
        Assert.assertEquals("SILVER", gml.get(0).getIsoformGeneMappings().get(1).getQuality());
        Assert.assertEquals(13, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().size());
        Assert.assertEquals(1, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(0).getFirstPositionOnGene());
        Assert.assertEquals(153, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(0).getLastPositionOnGene());
        Assert.assertEquals(38813, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(12).getFirstPositionOnGene());
        Assert.assertEquals(39503, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(12).getLastPositionOnGene());
    }

    @Test
    public void exonMappingIsoformInfoShouldContainAQuality() {

        ExonMapping exonMapping = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_O94759");

        Assert.assertTrue(exonMapping.getMappedIsoformInfos().values().stream().allMatch(map -> map.containsKey("quality")));
        Assert.assertFalse(exonMapping.isLowQualityMappings());
    }

    @Test
    public void testNX_Q07157ExonMapping() {

        ExonMapping exonMapping = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_Q07157");

        Assert.assertTrue(exonMapping.isLowQualityMappings());
    }

    private void assertExonStructures(List<IsoformGeneMapping> iml, String isoName, String expEnsg, String expEnst, String expIsoPosOnRefGene, String expExonPosOnRefGene) {

        IsoformGeneMapping isoformGeneMapping = iml.stream()
                .filter(im -> im.getIsoformAccession().equals(isoName))
                .collect(Collectors.toList()).get(0);

        Assert.assertEquals(expEnsg, isoformGeneMapping.getReferenceGeneName());

        String isoPosOnRefGene = isoformGeneMapping.getIsoformGeneRegionMappings().stream()
                .map(k -> k.getFirstPosition()+"-"+k.getLastPosition())
                .collect(Collectors.joining(","));

        Assert.assertEquals(expIsoPosOnRefGene, isoPosOnRefGene);

        TranscriptGeneMapping transcriptGeneMapping = isoformGeneMapping.getTranscriptGeneMappings().stream()
                .filter(tm -> tm.getName().equals(expEnst))
                .collect(Collectors.toList()).get(0);

        String exonPosOnRefGene = transcriptGeneMapping.getExons().stream()
                .map(e -> e.getFirstPositionOnGene()+"-"+e.getLastPositionOnGene())
                .collect(Collectors.joining(","));

        Assert.assertEquals(expExonPosOnRefGene, exonPosOnRefGene);

    }
}