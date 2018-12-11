package org.nextprot.api.tasks;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.mol.Protein;
import org.expasy.mzjava.proteomics.mol.digest.LengthDigestionController;
import org.expasy.mzjava.proteomics.mol.digest.Protease;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.nextprot.api.commons.app.CommandLineSpringParser;
import org.nextprot.api.commons.app.SpringBasedTask;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;

import java.util.List;

/**
 * Digest all neXtProt isoform sequences
 *
 * See also sources of mzjava-proteomics are available at https://bitbucket.org/sib-pig/mzjava-proteomics
 */
public class ProteinDigestion extends SpringBasedTask<ProteinDigestion.ArgumentParser> {

    private ProteinDigestion(String[] args) throws ParseException {

        super(args);
    }

    @Override
    protected ArgumentParser newCommandLineParser() {
        return new ArgumentParser("ProteinDigestion");
    }

    @Override
    protected void execute() {

	    IsoformService isoformService = getBean(IsoformService.class);
        MasterIdentifierService masterIdentifierService = getBean(MasterIdentifierService.class);

	    ProteinDigester digester = new ProteinDigester.Builder(Protease.TRYPSIN)
			    .controller(new LengthDigestionController(7, 77))
			    .missedCleavageMax(2)
			    .build();

        for (String entryAccession : masterIdentifierService.findUniqueNames()) {

	        for (Isoform isoform : isoformService.findIsoformsByEntryName(entryAccession)) {

		        List<Peptide> peptides = digester.digest(new Protein(isoform.getIsoformAccession(), isoform.getSequence()));

		        System.out.println(peptides);
	        }
        }
    }

    /**
     * Parse arguments and provides MainConfig object
     */
    static class ArgumentParser extends CommandLineSpringParser {

        public ArgumentParser(String appName) {
            super(appName);
        }

        @Override
        protected Options createOptions() {

            Options options = super.createOptions();

            return options;
        }

        @Override
        protected void parseOtherParams(CommandLine commandLine) throws ParseException {

        }

        @Override
        public String toString() {
            return  "Parameters\n";
        }
    }

    public static void main(String[] args) throws Exception {

        new ProteinDigestion(args).run();
    }
}
