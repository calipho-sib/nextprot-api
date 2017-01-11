package org.nextprot.api.blast.service.impl;

import org.nextprot.api.blast.domain.gen.*;
import org.nextprot.api.blast.service.BlastResultUpdaterService;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.MainNamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Modify and customize blast output.
 *
 * <ul>
 * <li>Remove useless fields (ex: blast paper reference)</li>
 * <li>Add some new fields (ex: sequence query, matching isoform accession,...)</li>
 * </ul>
 */
@Service
public class BlastResultUpdaterServiceImpl implements BlastResultUpdaterService {

    private final static Pattern ISOFORM_ACCESSION_PATTERN = Pattern.compile("^.*(NX_[^-]+)(-\\d+).*$");
    private final static DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##");

    @Autowired
    private MainNamesService mainNamesService;

    @Override
    public void update(Report blastReport, String proteinSequence) {

        if (blastReport == null) {
            throw new NextProtException("nothing to update: blast result report was not defined");
        }

        updateReport(blastReport);
        updateParams(blastReport.getParams(), proteinSequence);

        Search search = blastReport.getResults().getSearch();
        updateSearch(search);

        for (Hit hit : search.getHits()) {

            updateHit(hit);
            hit.getDescription().forEach(this::updateHitDescription);
            hit.getHsps().forEach(hsp -> this.updateHsp(hsp, proteinSequence));
        }

        updateStat(search.getStat());
    }

    @Override
    public void updateDescription(Description description, String isoAccession, String entryAccession) {

        MainNames entryNames = mainNamesService.findIsoformOrEntryMainName(entryAccession);
        MainNames isoNames = mainNamesService.findIsoformOrEntryMainName(isoAccession);

        if (entryNames == null)
            throw new NextProtException("could not find informations for entry "+entryAccession);
        if (isoNames == null)
            throw new NextProtException("could not find informations for isoform "+isoAccession);

        String geneName = (!entryNames.getGeneNameList().isEmpty()) ? entryNames.getGeneNameList().get(0) : null;
        String proteinName = entryNames.getName();
        String title = proteinName + ((geneName != null) ? " ("+geneName+")":"")+" ["+isoNames.getAccession()+"]";

        description.setTitle(title);
        description.setEntryAccession(entryNames.getAccession());
        description.setIsoAccession(isoNames.getAccession());
        description.setIsoName(isoNames.getName());
        description.setProteinName(proteinName);
        description.setGeneName(geneName);
    }

    private void updateReport(Report report) {

        report.setProgram(null);
        report.setReference(null);
        report.setSearchTarget(null);
    }

    private void updateParams(Params params, String proteinSequence) {

        //params.setMatrix(null);
        //params.setExpect(null);
        //params.setGapOpen(null);
        //params.setGapExtend(null);
        params.setSequence(proteinSequence);
    }

    private void updateSearch(Search search) {

        search.setQueryId(null);
        search.setQueryTitle(null);
        search.setQueryLen(null);
    }

    private void updateHit(Hit hit) {

        hit.setNum(null);
    }

    private void updateHitDescription(Description description) {

        description.setId(null);
        description.setAccession(null);

        Matcher matcher = ISOFORM_ACCESSION_PATTERN.matcher(description.getTitle());

        if (matcher.find()) {

            String entryAccession = matcher.group(1);
            String isoformAccession = entryAccession + matcher.group(2);

            updateDescription(description, isoformAccession, entryAccession);
        }
        else {
            throw new NextProtException("blast db error: could not extract isoform information from header "+description.getTitle());
        }
    }

    private void updateHsp(Hsp hsp, String proteinSequence) {

        float identityPercent = (float) hsp.getIdentity() / proteinSequence.length() * 100;

        hsp.setIdentityPercent(Float.parseFloat(PERCENT_FORMAT.format(identityPercent)));
        hsp.setNum(null);
    }

    private void updateStat(Stat stat) {

        stat.setHspLen(null);
        stat.setEffSpace(null);
        stat.setKappa(null);
        stat.setLambda(null);
        stat.setEntropy(null);
    }
}
