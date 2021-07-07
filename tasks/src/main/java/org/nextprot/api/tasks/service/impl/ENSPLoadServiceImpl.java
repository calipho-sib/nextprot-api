package org.nextprot.api.tasks.service.impl;

import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.tasks.service.ENSPLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ENSPLoadServiceImpl implements ENSPLoadService {

    @Autowired
    private IsoformService isoformService;

    @Autowired
    private MasterIdentifierService masterIdentifierService;

    @Autowired
    private GenomicMappingService genomicMappingService;

    @Override
    public void loadENSPSequences() {
        // Get all the nextprot entries
        Set<String> entryAccessions = masterIdentifierService.findUniqueNames();

        // Get the isoforms for each entry
        entryAccessions.stream()
                .forEach((entryAccession -> System.out.println(entryAccession)));

        // Get the ENSGs for isoforms



    }

    private GenomicMapping getGenomicMappingOfEnsgAlignedWithEntry(String entryAc) {
        List<GenomicMapping> gmaps = genomicMappingService.findGenomicMappingsByEntryName(entryAc);
        if (gmaps==null) return null;
        for (GenomicMapping gm : gmaps) {
            if (gm.isChosenForAlignment()) return gm;
        }
        return null;
    }

    private Map<String,String> getEnstAlignedWithIsoform(String entryAccession) {
        Map<String,String> result = new HashMap<>();
        result.put("ENSG", "---------------");
        result.put("ENST", "---------------");
        result.put("ENSP", "---------------");
        result.put("quality", "----");
        GenomicMapping gm = getGenomicMappingOfEnsgAlignedWithEntry(entryAccession);
        if (gm==null) return result;
        result.put("ENSG", gm.getAccession());
        for (IsoformGeneMapping igm : gm.getIsoformGeneMappings()) {
            System.out.println(igm.getIsoformAccession());
                List<TranscriptGeneMapping> tgmList = igm.getTranscriptGeneMappings();
                if (tgmList==null || tgmList.size()==0) return result;
                // the first mapping in list is the shortest and is always chosen as "main" (or best) transcript
                TranscriptGeneMapping tgm = tgmList.get(0);
                result.put("ENST", tgm.getDatabaseAccession());
                if (tgm.getProteinId()!=null) result.put("ENSP", tgm.getProteinId());
                result.put("quality", tgm.getQuality());
                return result;
        }
        return null;
    }
}
