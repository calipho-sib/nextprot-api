package org.nextprot.api.blast.service.impl;

import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.domain.BlastPRunner;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.blast.service.BlastPService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlastPServiceImpl implements BlastPService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    private MainNamesService mainNamesService;

    @Override
    public BlastResult blastProteinSequence(BlastPConfig config, String header, String sequence) {

        return new BlastPRunner(config).run(header, sequence, mainNamesService);
    }

    @Override
    public BlastResult blastIsoformSequence(BlastPConfig config, String isoformAccession, Integer begin1BasedIndex, Integer end1BasedIndex) {

        if (!isoformAccession.contains("-")) {
            // TODO: bad format isoform name
        }

        String entryAccession = isoformAccession.split("-")[0];

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withTargetIsoforms());

        Isoform isoform = IsoformUtils.getIsoformByName(entry, isoformAccession);
        String isoformSequence = isoform.getSequence();

        int begin = (begin1BasedIndex != null) ? begin1BasedIndex : 1;
        int end = (end1BasedIndex != null) ? end1BasedIndex : isoformSequence.length();

        // swap indices if needed
        if (begin > end) {

            int tmp = begin;
            begin = end;
            end = tmp;
        }

        if (end > isoformSequence.length()) {
            // TODO: out of bound error
        }

        // format header
        StringBuilder header = new StringBuilder();

        if (end - begin + 1 < isoformSequence.length()) {

            header.append("Selection of ").append(begin).append("-").append(end).append(" ");
        }
        header.append("from protein ").append(entryAccession).append(", isoform ").append(isoform.getMainEntityName().getName());

        return blastProteinSequence(config, header.toString(), isoformSequence.substring(begin-1, end));
    }
}
