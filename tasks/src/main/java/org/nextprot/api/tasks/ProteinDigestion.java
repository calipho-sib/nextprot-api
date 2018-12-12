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
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;

import java.util.List;
import java.util.stream.Collectors;

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
	    AnnotationService annotationService = getBean(AnnotationService.class);

	    ProteinDigester digester = new ProteinDigester.Builder(Protease.TRYPSIN)
			    .controller(new LengthDigestionController(7, 77))
			    .missedCleavageMax(2)
			    .build();

        for (String entryAccession : masterIdentifierService.findUniqueNames()) {

	        // filter all isoform accessions that have an initiator methionine
	        List<String> isoAccessionInitMethList = annotationService.findAnnotations(entryAccession).stream()
			        .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.INITIATOR_METHIONINE)
			        .map(annotation -> annotation.getTargetingIsoformsMap().keySet())
			        .flatMap(annotations -> annotations.stream())
			        .collect(Collectors.toList());

	        for (Isoform isoform : isoformService.findIsoformsByEntryName(entryAccession)) {

	        	// the sequence of isoform proteins with initiator methionine should be truncated
	        	String isoformSequence = (isoAccessionInitMethList.contains(isoform.getIsoformAccession()) ?
				        isoform.getSequence().substring(1) : isoform.getSequence());

		        List<Peptide> peptides = digester.digest(new Protein(isoform.getIsoformAccession(), isoformSequence));

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
