package org.nextprot.api.blast.service.impl;

import org.nextprot.api.blast.dao.BlastDAO;
import org.nextprot.api.blast.domain.*;
import org.nextprot.api.blast.domain.gen.Description;
import org.nextprot.api.blast.domain.gen.Report;
import org.nextprot.api.blast.service.*;
import org.nextprot.api.commons.utils.ExceptionWithReason;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BlastServiceImpl implements BlastService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    private BlastDAO blastDAO;

    @Autowired
    private BlastResultUpdaterService blastResultUpdaterService;

    @Override
    public BlastProgramOutput blastProteinSequence(BlastSequenceInput params) {

        try {
            BlastPRunner.FastaEntry fastaEntry =
                    new BlastPRunner.FastaEntry(params.getHeader(), params.getSequence());

            Report result = new BlastPRunner(params).run(fastaEntry);

            blastResultUpdaterService.update(result, fastaEntry.getSequence());

            return new BlastProgramSuccess(params, result);
        } catch (ExceptionWithReason exceptionWithReason) {

            return new BlastProgramFailure(params, exceptionWithReason);
        }
    }

    @Override
    public BlastProgramOutput blastIsoformSequence(BlastIsoformInput params) {

        String isoformAccession = params.getIsoformAccession();
        String entryAccession = isoformAccession.split("-")[0];

        try {
            Isoform isoform = getIsoform(isoformAccession, entryAccession);

            params.setSequence(isoform.getSequence());
            params.validateSequencePositions();
            params.setSequence(params.getSequence().substring(params.getBeginPos()-1, params.getEndPos()));
            params.setHeader(buildHeader(params, isoform, entryAccession));
            params.setEntryAccession(entryAccession);

            Description queryDescription = new Description();
            blastResultUpdaterService.updateDescription(queryDescription, isoformAccession, entryAccession);
            params.setDescription(queryDescription);

            return blastProteinSequence(params);

        } catch (ExceptionWithReason exceptionWithReason) {

            return new BlastProgramFailure(params, exceptionWithReason);
        }
    }

    @Override
    public BlastProgramOutput makeNextprotBlastDb(BlastProgram.Params params) {

        BlastDbMaker runner = new BlastDbMaker(params);

        try {
            String result = runner.run(blastDAO.getAllIsoformSequences());

            return new BlastProgramSuccess(params, result);
        } catch (ExceptionWithReason exceptionWithReason) {

            return new BlastProgramFailure(params, exceptionWithReason);
        }
    }

    private Isoform getIsoform(String isoformAccession, String entryAccession) throws ExceptionWithReason {

        Isoform isoform = IsoformUtils.getIsoformByName(entryBuilderService.build(EntryConfig.newConfig(entryAccession).withTargetIsoforms()), isoformAccession);

        if (isoform == null) {

            throw ExceptionWithReason.withReason("unknown isoform", isoformAccession+": could not find isoform from entry "+entryAccession);
        }

        return isoform;
    }

    private String buildHeader(BlastIsoformInput config, Isoform isoform, String entryAccession) {

        // format header
        StringBuilder header = new StringBuilder();

        if (config.calcQuerySeqLength() < isoform.getSequenceLength()) {

            header.append("Selection of ").append(config.getQuerySeqBegin()).append("-").append(config.getQuerySeqEnd()).append(" ");
        }
        header.append("from protein ").append(entryAccession).append(", isoform ").append(isoform.getMainEntityName().getName());

        return header.toString();
    }
}
