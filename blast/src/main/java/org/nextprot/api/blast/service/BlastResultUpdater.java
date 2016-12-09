package org.nextprot.api.blast.service;

import com.google.common.base.Preconditions;
import org.nextprot.api.blast.domain.gen.*;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.MainNamesService;

import java.text.DecimalFormat;
import java.util.Objects;
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
public class BlastResultUpdater {

    private final static Pattern ISOFORM_ACCESSION_PATTERN = Pattern.compile("^.+(NX_[^-]+)(-\\d+).*$");
    private final static DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##");

    private final MainNamesService mainNamesService;
    private final String sequence;

    public BlastResultUpdater(MainNamesService mainNamesService, String sequence) {

        Objects.requireNonNull(mainNamesService);
        Objects.requireNonNull(sequence);
        Preconditions.checkArgument(!sequence.isEmpty());

        this.mainNamesService = mainNamesService;
        this.sequence = sequence;
    }

    /**
     * Update BlastResult object
     * @param blastResult the original blast output
     */
    public void update(BlastResult blastResult) {

        if (blastResult == null) {
            throw new NextProtException("nothing to update: blast result was not defined");
        }

        Report report = blastResult.getBlastOutput2().get(0).getReport();
        Search search = report.getResults().getSearch();

        updateReport(report);
        updateSearch(search);

        for (Hit hit : search.getHits()) {

            updateHit(hit);
            hit.getDescription().forEach(this::updateDescription);
            hit.getHsps().forEach(this::updateHsp);
        }

        updateStat(search.getStat());
    }

    protected void updateReport(Report report) {

        report.setProgram(null);
        report.setReference(null);
        report.setSearchTarget(null);
    }

    protected void updateSearch(Search search) {

        search.setQueryId(null);
        search.setQueryTitle(null);
        search.setQueryLen(null);
    }

    protected void updateHit(Hit hit) {

        hit.setNum(null);
    }

    protected void updateDescription(Description description) {

        description.setId(null);
        description.setAccession(null);

        Matcher matcher = ISOFORM_ACCESSION_PATTERN.matcher(description.getTitle());

        // isoform sequence only
        if (matcher.find()) {

            String entryAccession = matcher.group(1);
            String isoformAccession = entryAccession + matcher.group(2);

            setAccessions(description, isoformAccession, entryAccession);
        }
    }

    private void setAccessions(Description description, String isoAccession, String entryAccession) {

        MainNames entryNames = mainNamesService.findIsoformOrEntryMainName(entryAccession);
        MainNames isoNames = mainNamesService.findIsoformOrEntryMainName(isoAccession);

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

    protected void updateHsp(Hsp hsp) {

        float identityPercent = (float) hsp.getIdentity() / sequence.length() * 100;

        hsp.setIdentityPercent(Float.parseFloat(PERCENT_FORMAT.format(identityPercent)));
        hsp.setNum(null);
    }

    protected void updateStat(Stat stat) {

        stat.setHspLen(null);
        stat.setEffSpace(null);
        stat.setKappa(null);
        stat.setLambda(null);
        stat.setEntropy(null);
    }
}
