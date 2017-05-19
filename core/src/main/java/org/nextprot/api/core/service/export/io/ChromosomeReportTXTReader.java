package org.nextprot.api.core.service.export.io;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.service.export.ChromosomeReportReader;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Writes a {@code ChromosomeReport} in TXT format
 *[\w-]+)\s+(NX_\w+)\s+([\d\w.-]+
 * Created by fnikitin on 19.04.17.
 */
public class ChromosomeReportTXTReader implements ChromosomeReportReader {

    private static final Pattern CHROMOSOME_NAME_PATTERN = Pattern.compile("^Description:\\s+Chromosome\\s+(\\S+)\\s+report$");
    private static final Pattern RELEASE_DATE_PATTERN    = Pattern.compile("^Release:\\s+(.+)$");
    private static final Pattern ENTRY_COUNT_PATTERN     = Pattern.compile("^Total number of entries:\\s+(\\d+)$");
    private static final Pattern GENE_COUNT_PATTERN      = Pattern.compile("^Total number of genes:\\s+(\\d+)$");
    private static final Pattern ENTRY_VALUES_PATTERN    = Pattern.compile("^" +
                "([\\w-]+)\\s+"                    + // Gene name
                "(NX_\\w+)\\s+"                    + // neXtProt AC
                "([\\d\\w.-]+)\\s+"                + // Chromosomal position
                "([\\d-]+)\\s+"                    + // Start position
                "([\\d-]+)\\s+"                    + // Stop position
                "((?:\\bprotein level\\b)|(?:\\btranscript level\\b)|(?:\\bhomology\\b)|(?:\\bpredicted\\b)|(?:\\buncertain\\b))\\s+" + // Protein existence
                "((?:\\byes\\b)|(?:\\bno\\b))\\s+" + // Proteomics
                "((?:\\byes\\b)|(?:\\bno\\b))\\s+" + // Antibody
                "((?:\\byes\\b)|(?:\\bno\\b))\\s+" + // 3D
                "((?:\\byes\\b)|(?:\\bno\\b))\\s+" + // Disease
                "(\\d+)\\s+"                       + // Isoforms
                "(\\d+)\\s+"                       + // Variants
                "(\\d+)\\s+"                       + // PTMs
                "(.+)$");                            // Description

    @Override
    public ChromosomeReport read(Reader reader) throws ParseException {

        ChromosomeReport report = new ChromosomeReport();
        ChromosomeReport.Summary summary = new ChromosomeReport.Summary();
        List<EntryReport> entryReports = new ArrayList<>();

        try {
            MatchConsumer matchConsumer = new MatchConsumer(reader);

            matchConsumer.consumeNextMatchOfThrowException("chromosome name",
                    CHROMOSOME_NAME_PATTERN, (matcher -> summary.setChromosome(matcher.group(1))));
            matchConsumer.consumeNextMatchOfThrowException("release date",
                    RELEASE_DATE_PATTERN, (matcher -> report.setDataRelease(matcher.group(1))));
            matchConsumer.consumeNextMatchOfThrowException("entry count",
                    ENTRY_COUNT_PATTERN, (matcher -> summary.setEntryCount(Integer.parseInt(matcher.group(1)))));
            matchConsumer.consumeNextMatchOfThrowException("gene count",
                    GENE_COUNT_PATTERN, (matcher -> summary.setGeneCount(Integer.parseInt(matcher.group(1)))));

            EntryReportConsumer entryReportConsumer = new EntryReportConsumer(entryReports);

            boolean moreLinesToRead;
            do {
                moreLinesToRead = matchConsumer.consumeNextMatch(ENTRY_VALUES_PATTERN, entryReportConsumer);
            } while (moreLinesToRead);

            report.setSummary(summary);
            report.setEntryReports(entryReports);

            return report;
        }
        catch (IOException e) {

            throw new ParseException(e.getMessage(), -1);
        }
    }

    private static class MatchConsumer {

        private final LineNumberReader lnr;

        MatchConsumer(Reader reader) {

            lnr = new LineNumberReader(reader);
        }

        void consumeNextMatchOfThrowException(String valueToConsume, Pattern pattern, Consumer<Matcher> consumer) throws IOException, ParseException {

            int ln = lnr.getLineNumber();

            if (!consumeNextMatch(pattern, consumer)) {

                throw new ParseException("Could not parse "+valueToConsume+": reaching end of file (from line "+(ln+1)+")", -1);
            }
        }

        boolean consumeNextMatch(Pattern pattern, java.util.function.Consumer<Matcher> consumer) throws IOException {

            String line;

            while ((line = lnr.readLine()) != null) {

                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {

                    consumer.accept(matcher);
                    return true;
                }
            }

            return false;
        }
    }

    private static class EntryReportConsumer implements Consumer<Matcher> {

        private final List<EntryReport> entryReports;

        EntryReportConsumer(List<EntryReport> entryReports) {
            this.entryReports = entryReports;
        }

        @Override
        public void accept(Matcher matcher) {

            EntryReport entryReport = new EntryReport();

            entryReport.setAccession(matcher.group(2));
            entryReport.setChromosomalLocation(newChromosomalLocation(matcher.group(1), matcher.group(3), matcher.group(4), matcher.group(5)));
            entryReport.setProteinExistence(ProteinExistenceLevel.valueOfString(matcher.group(6)));
            entryReport.setPropertyTest(EntryReport.IS_PROTEOMICS, "yes".equals(matcher.group(7)));
            entryReport.setPropertyTest(EntryReport.IS_ANTIBODY, "yes".equals(matcher.group(8)));
            entryReport.setPropertyTest(EntryReport.IS_3D, "yes".equals(matcher.group(9)));
            entryReport.setPropertyTest(EntryReport.IS_DISEASE, "yes".equals(matcher.group(10)));
            entryReport.setPropertyCount(EntryReport.ISOFORM_COUNT, Integer.parseInt(matcher.group(11)));
            entryReport.setPropertyCount(EntryReport.VARIANT_COUNT, Integer.parseInt(matcher.group(12)));
            entryReport.setPropertyCount(EntryReport.PTM_COUNT, Integer.parseInt(matcher.group(13)));
            entryReport.setDescription(matcher.group(14));

            entryReports.add(entryReport);
        }

        private ChromosomalLocation newChromosomalLocation(String geneName, String chromosomalPosition,
                                                           String startPosition, String stopPosition) {
            try {
                ChromosomalLocation chromosomalLocation = ChromosomalLocation.fromString(chromosomalPosition);
                chromosomalLocation.setRecommendedName(geneName);
                if (!"-".equals(startPosition)) {
                    chromosomalLocation.setFirstPosition(Integer.parseInt(startPosition));
                }
                if (!"-".equals(stopPosition)) {
                    chromosomalLocation.setLastPosition(Integer.parseInt(stopPosition));
                }
                return chromosomalLocation;
            } catch (ParseException e) {
                throw new NextProtException(e.getMessage()+": can not instantiate ChromosomalLocation");
            }
        }
    }
}
