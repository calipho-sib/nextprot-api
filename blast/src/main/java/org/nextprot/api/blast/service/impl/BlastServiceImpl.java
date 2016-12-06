package org.nextprot.api.blast.service.impl;

import org.nextprot.api.blast.domain.BlastConfig;
import org.nextprot.api.blast.domain.BlastRunner;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.blast.service.BlastService;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MainNamesService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BlastServiceImpl implements BlastService {

    private static final String ISOFORM_REX_EXP= "^NX_[^-]+-\\d+$";

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    private MainNamesService mainNamesService;

    @Override
    public BlastResult blastProteinSequence(BlastConfig config, String header, String sequence) {

        return new BlastRunner(config).run(header, sequence, mainNamesService);
    }

    @Override
    public BlastResult blastIsoformSequence(BlastConfig config, String isoformAccession, Integer begin1BasedIndex, Integer end1BasedIndex) {

        if (!isoformAccession.matches(ISOFORM_REX_EXP)) {
            throw new NextProtException(isoformAccession+": invalid isoform accession (format: "+ISOFORM_REX_EXP+")");
        }

        String entryAccession = isoformAccession.split("-")[0];

        Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withTargetIsoforms());

        Isoform isoform = IsoformUtils.getIsoformByName(entry, isoformAccession);
        if (isoform == null) {
            throw new NextProtException(isoformAccession+": could not find isoform from entry "+entryAccession);
        }

        String isoformSequence = isoform.getSequence();

        // assign default positions
        int begin = (begin1BasedIndex != null) ? begin1BasedIndex : 1;
        int end = (end1BasedIndex != null) ? end1BasedIndex : isoformSequence.length();

        // swap indices if needed
        if (begin > end) {

            int tmp = begin;
            begin = end;
            end = tmp;
        }

        // check positions
        if (begin <= 0 || begin > isoformSequence.length()) {
            throw new NextProtException("begin position "+begin+" is out of bound (should be > 0 and <= "+isoformSequence.length()+")");
        }
        if (end <= 0 || end > isoformSequence.length()) {
            throw new NextProtException("end position "+end+" is out of bound (should be > 0 and <= "+isoformSequence.length()+")");
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
