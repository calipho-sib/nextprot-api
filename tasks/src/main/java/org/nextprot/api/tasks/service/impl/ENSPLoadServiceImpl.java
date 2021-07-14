package org.nextprot.api.tasks.service.impl;

import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformGeneMapping;
import org.nextprot.api.core.domain.TranscriptGeneMapping;
import org.nextprot.api.core.service.GenomicMappingService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.tasks.service.ENSPLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ENSPLoadServiceImpl implements ENSPLoadService {

    @Autowired
    private IsoformService isoformService;

    @Autowired
    private MasterIdentifierService masterIdentifierService;

    @Autowired
    private GenomicMappingService genomicMappingService;

    @Override
    public List<Map<String, Object>> loadENSPSequences() {
        // Get all the nextprot entries
        Set<String> entryAccessions = masterIdentifierService.findUniqueNames();

        List<Map<String, Object>> entries = new ArrayList<>();
        entryAccessions.stream()
                .forEach((entryAccession -> {
                    System.out.println("Entry " + entryAccession + "---------");
                    // Get the isoforms for each entry
                    List isoforms = new ArrayList();
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("entry", entryAccession);

                    for (Isoform isoform : isoformService.findIsoformsByEntryName(entryAccession)) {
                        Map<String, String> isoformMappings = new HashMap<>();

                        String isoformAccession = isoform.getIsoformAccession();
                        Map<String, String> result = getEnstAlignedWithIsoform(isoformAccession);

                        if(result != null) {
                            String ensg = result.get("ENSG");
                            String enst = result.get("ENST");
                            String ensp = result.get("ENSP");
                            System.out.println(isoformAccession + "," + ensg  + "," + enst + "," + ensp);

                            entry.put("ENSG", ensg);
                            isoformMappings.put("isoform", isoformAccession);
                            isoformMappings.put("sequence", isoform.getSequence());
                            isoformMappings.put("ENST", enst);
                            isoformMappings.put("ENSP", ensp);
                            isoforms.add(isoformMappings);
                            entry.put("isoforms", isoforms);
                        } else {
                            System.out.println("No aligned isoforms for the entry " + entryAccession);
                        }
                    }
                    entries.add(entry);
                    System.out.println("---------");
                }));


        return entries;
    }

    private GenomicMapping getGenomicMappingOfEnsgAlignedWithEntry(String entryAc) {
        List<GenomicMapping> gmaps = genomicMappingService.findGenomicMappingsByEntryName(entryAc);
        if (gmaps==null) return null;
        for (GenomicMapping gm : gmaps) {
            if (gm.isChosenForAlignment()) return gm;
        }
        return null;
    }

    private Map<String,String> getEnstAlignedWithIsoform(String isoformAccession) {
        Map<String,String> result = new HashMap<>();
        result.put("ENSG", "---------------");
        result.put("ENST", "---------------");
        result.put("ENSP", "---------------");
        result.put("quality", "----");
        String entryAc = isoformAccession.split("-")[0];
        GenomicMapping gm = getGenomicMappingOfEnsgAlignedWithEntry(entryAc);
        if (gm==null) return result;
        result.put("ENSG", gm.getAccession());
        for (IsoformGeneMapping igm : gm.getIsoformGeneMappings()) {
            if (igm.getIsoformAccession().equals(isoformAccession)) {
                List<TranscriptGeneMapping> tgmList = igm.getTranscriptGeneMappings();
                if (tgmList==null || tgmList.size()==0) return result;
                // the first mapping in list is the shortest and is always chosen as "main" (or best) transcript
                TranscriptGeneMapping tgm = tgmList.get(0);
                result.put("ENST", tgm.getDatabaseAccession());
                if (tgm.getProteinId()!=null) result.put("ENSP", tgm.getProteinId());
                result.put("quality", tgm.getQuality());
                return result;
            }
        }
        return null;
    }
}
