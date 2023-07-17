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

    HashMap<String, Integer> statistics = new LinkedHashMap<>();

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
                    updateStatistics("entry", 1);

                    int mappedIsoforms = 0;
                    for (Isoform isoform : isoformService.findIsoformsByEntryName(entryAccession)) {
                        List isoformMappings = new ArrayList<Map>();

                        String isoformAccession = isoform.getIsoformAccession();
                        Map isoformMapping = new HashMap();
                        isoformMapping.put("isoform", isoformAccession);
                        isoformMapping.put("sequence", isoform.getSequence());


                        List<Map<String, String>> results = getEnstAlignedWithIsoform(isoformAccession);
                        if(results != null) {
                            for(Map result : results) {
                                if(result != null) {
                                    String ensg = (String) result.get("ENSG");
                                    String enst = (String) result.get("ENST");
                                    String ensp = (String) result.get("ENSP");
                                    System.out.println("Aligned " + isoformAccession + "," + ensg  + "," + enst + "," + ensp);

                                    entry.put("ENSG", ensg);
                                    isoformMapping.put("ENST", enst);
                                    isoformMapping.put("ENSP", ensp);
                                    isoformMappings.add(isoformMapping);
                                    mappedIsoforms++;
                                    updateStatistics("Aligned isoform", 1);
                                } else {
                                    System.out.println("No aligned ENST for isform " + isoformAccession);
                                    updateStatistics("No aligned ENST", 1);
                                }
                            }
                            isoforms.add(isoformMappings);
                            entry.put("isoforms", isoforms);
                        }
                    }
                    if(mappedIsoforms == 0) {
                        updateStatistics("Full entry without mappings", 1);
                    }
                    entries.add(entry);
                    System.out.println("---------");
                }));
        printStatics();
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

    private List<Map<String,String>> getEnstAlignedWithIsoform(String isoformAccession) {

        String entryAc = isoformAccession.split("-")[0];
        GenomicMapping gm = getGenomicMappingOfEnsgAlignedWithEntry(entryAc);
        if (gm==null) return null;

        List results = new ArrayList<Map>();
        for (IsoformGeneMapping igm : gm.getIsoformGeneMappings()) {
            if (igm.getIsoformAccession().equals(isoformAccession)) {
                List<TranscriptGeneMapping> tgmList = igm.getTranscriptGeneMappings();
                if (tgmList==null || tgmList.size()==0) return null;
                Map<String,String> result = new HashMap<>();
                result.put("ENSG", gm.getAccession());
                // the first mapping in list is the shortest and is always chosen as "main" (or best) transcript
                TranscriptGeneMapping tgm = tgmList.get(0);
                result.put("ENST", tgm.getDatabaseAccession());
                if (tgm.getProteinId()!=null) result.put("ENSP", tgm.getProteinId());
                result.put("quality", tgm.getQuality());
                results.add(result);
            }
        }
        return results;
    }

    private void updateStatistics(String key, int statistic) {
        if(!statistics.containsKey(key)) {
            statistics.put(key, 1);
        } else {
            Integer currentValue = statistics.get(key);
            statistics.put(key, currentValue + 1);
        }
    }

    private void printStatics() {
        for(String key : statistics.keySet()) {
            System.out.println(key + " " + statistics.get(key));
        }
    }
}
