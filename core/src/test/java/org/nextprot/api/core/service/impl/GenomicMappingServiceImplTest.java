package org.nextprot.api.core.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.domain.exon.CategorizedExon;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.service.EntryExonMappingService;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
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
                "358-429,1201-1241,1693-1780,5873-5921,6089-6126,6130-6475,42406-42528,46275-46394,47398-47517,49075-49194,53017-53140,53225-53420,56906-58173");
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
        Assert.assertEquals(7030, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(0).getFirstPositionOnGene());
        Assert.assertEquals(7175, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(0).getLastPositionOnGene());
        Assert.assertEquals(45835, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(12).getFirstPositionOnGene());
        Assert.assertEquals(51047, gml.get(0).getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons().get(12).getLastPositionOnGene());
    }

    @Test
    public void exonMappingIsoformInfoShouldContainAQuality() {

        ExonMapping exonMapping = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_O94759");

        Assert.assertTrue(exonMapping.getMappedIsoformInfos().values().stream().allMatch(map -> map.containsKey("quality")));
        Assert.assertFalse(exonMapping.isLowQualityMappings());
    }

    @Test
    public void testIsoformNX_Q07157BronzeExonMappings() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_Q07157");

        Assert.assertEquals(2, gml.get(0).getIsoformGeneMappings().size());

        GenomicMapping gm = gml.get(0);

        List<CategorizedExon> exonsForLong = gm.getIsoformGeneMappings().get(0).getTranscriptGeneMappings().get(0).getExons();
        List<CategorizedExon> exonsForShort = gm.getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons();

        Assert.assertEquals(28, exonsForLong.size());
        Assert.assertEquals(27, exonsForShort.size());

        Assert.assertEquals(27, exonsForLong.get(0).getGeneRegion().getLength());
        Assert.assertEquals(57, exonsForLong.get(1).getGeneRegion().getLength());
        Assert.assertEquals(2323, exonsForLong.get(27).getGeneRegion().getLength());

        Assert.assertEquals(27, exonsForShort.get(0).getGeneRegion().getLength());
        Assert.assertEquals(57, exonsForShort.get(1).getGeneRegion().getLength());
        Assert.assertEquals(95, exonsForShort.get(26).getGeneRegion().getLength());
    }

    @Test
    public void testNX_Q07157ExonMappingQualityShouldBeLow() {

        ExonMapping exonMapping = entryExonMappingService.findExonMappingGeneXIsoformXShorterENST("NX_Q07157");

        Assert.assertTrue(exonMapping.isLowQualityMappings());
    }

    @Test
    public void testIsoformNX_P17405ExonMapping() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_P17405");

        Assert.assertEquals(3, gml.get(0).getIsoformGeneMappings().size());

        GenomicMapping gm = gml.get(0);

        List<CategorizedExon> exonsForIso1 = gm.getIsoformGeneMappings().get(0).getTranscriptGeneMappings().get(0).getExons();
        List<CategorizedExon> exonsForIso3 = gm.getIsoformGeneMappings().get(1).getTranscriptGeneMappings().get(0).getExons();
        List<CategorizedExon> exonsForIso4 = gm.getIsoformGeneMappings().get(2).getTranscriptGeneMappings().get(0).getExons();

        Assert.assertEquals(6, exonsForIso1.size());
        Assert.assertEquals(7, exonsForIso3.size());
        Assert.assertEquals(6, exonsForIso4.size());

        Assert.assertEquals(443, exonsForIso1.get(0).getGeneRegion().getLength());
        Assert.assertEquals(773, exonsForIso1.get(1).getGeneRegion().getLength());
        Assert.assertEquals(799, exonsForIso1.get(5).getGeneRegion().getLength());

        Assert.assertEquals(443, exonsForIso3.get(0).getGeneRegion().getLength());
        Assert.assertEquals(773, exonsForIso3.get(1).getGeneRegion().getLength());
        Assert.assertEquals(799, exonsForIso3.get(6).getGeneRegion().getLength());

        Assert.assertEquals(442, exonsForIso4.get(0).getGeneRegion().getLength());
        Assert.assertEquals(770, exonsForIso4.get(1).getGeneRegion().getLength());
        Assert.assertEquals(581, exonsForIso4.get(5).getGeneRegion().getLength());
    }

    @Test
    public void testThatAllGivenIsoformsDoesNotMap() {

        List<String> isoformAccessions = Arrays.asList("NX_O43521-15",
                "NX_O95084-2",
                "NX_P0DMV8-2",
                "NX_P31947-2",
                "NX_P51522-2",
                "NX_P80723-2",
                "NX_Q6ZMV5-2",
                "NX_Q8IXZ3-2",
                "NX_Q8IYU4-2",
                "NX_Q8N6I1-2",
                "NX_Q8TCT1-2",
                "NX_Q8WZ71-2",
                "NX_Q96CX3-2",
                "NX_Q9BRL6-2",
                "NX_Q9UKF2-2",
                "NX_Q9Y6B2-2",
                "NX_Q9Y6Z5-2");

        for (String isoformAccession : isoformAccessions) {

            String entryAccession = isoformAccession.split("-")[0];

            GenomicMapping gm = genomicMappingService.findGenomicMappingsByEntryName(entryAccession).get(0);

            Assert.assertTrue(gm.getIsoformGeneMappings().stream()
                    .noneMatch(igm -> igm.getIsoformAccession().equals(isoformAccession)));
        }
    }

    @Test
    public void testIsoformsGeneMapping() {

        List<GenomicMapping> gml = genomicMappingService.findGenomicMappingsByEntryName("NX_O95084");

        Assert.assertEquals(1, gml.get(0).getIsoformGeneMappings().size());
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