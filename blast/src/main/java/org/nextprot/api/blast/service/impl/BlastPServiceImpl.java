package org.nextprot.api.blast.service.impl;

import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.domain.BlastPRunner;
import org.nextprot.api.blast.service.BlastPService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BlastPServiceImpl implements BlastPService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Override
    public String blastProteinSequence(BlastPConfig config, String header, String sequence) {

        return new BlastPRunner(config).run(header, sequence);
    }

    @Override
    public Map<String, String> blastEntry(BlastPConfig config, String entryName) {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withTargetIsoforms());

        Map<String, String> results = new HashMap<>();

        for (Isoform isoform : entry.getIsoforms()) {

            results.put(isoform.getIsoformAccession(), blastIsoform(config, isoform, 0, isoform.getSequenceLength()-1));
        }

        return results;
    }

    @Override
    public String blastIsoform(BlastPConfig config, String isoformName, Integer from, Integer to) {

        if (!isoformName.contains("-")) {
            // bad format isoform name
        }

        String entryName = isoformName.split("-")[0];

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withTargetIsoforms());

        return blastIsoform(config, IsoformUtils.getIsoformByName(entry, isoformName), from, to);
    }

    private String blastIsoform(BlastPConfig config, Isoform isoform, Integer from, Integer to) {

        String sequence = isoform.getSequence();

        int fromIndex = (from != null) ? from : 0;
        int toIndex = (to != null) ? to : sequence.length()-1;

        // swap positions
        if (fromIndex > toIndex) {

            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (toIndex >= sequence.length()) {
            // out of bound error
        }

        return blastProteinSequence(config, isoform.getIsoformAccession()+": "+fromIndex+"-"+toIndex, sequence.substring(fromIndex, toIndex+1));
    }
}
