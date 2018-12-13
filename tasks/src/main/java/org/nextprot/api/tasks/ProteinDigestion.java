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
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

	    Set<String> allEntries = masterIdentifierService.findUniqueNames();
	    List<Peptide> allPeptides = new ArrayList<>();
	    Integer ecnt = 0;
	    
	    System.err.println("Digesting " + allEntries.size() + " entries...");
	    for (String entryAccession : allEntries) {
        	//String entryAccession = "NX_Q14353";
	    	// We digest mature chains and propeptides
       	    List<Annotation> annotlist = annotationService.findAnnotations(entryAccession).stream()
        			 .filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MATURE_PROTEIN || annotation.getAPICategory() == AnnotationCategory.MATURATION_PEPTIDE)
        			 .collect(Collectors.toList());
       	    
	        for (Isoform isoform : isoformService.findIsoformsByEntryName(entryAccession)) {
	        	String isoformSequence = isoform.getSequence();
	        	String isoformAcc = isoform.getIsoformAccession();	        	
	        	 
	        	 for (Annotation annot : annotlist) {
	        		 Integer start = annot.getStartPositionForIsoform(isoformAcc);
	        		 Integer end = annot.getEndPositionForIsoform(isoformAcc);
	        		 if (start != null && end != null)
	        		 digester.digest(new Protein(isoformAcc, isoformSequence.substring(start,end)), allPeptides);
	        	 }
	        }
	        if(ecnt++ % 100 == 0) System.err.println(ecnt + " entries so far...");
        }
	        
	        // Collect the unique set of all peptides
	        Set<String> uniqpeptides = allPeptides.stream()
	        		.map(peptide -> peptide.toSymbolString())
	        		.collect(Collectors.toSet());

	        //System.out.println(uniqpeptides);
	        System.err.println(uniqpeptides.size() + "unique peptides...");
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
