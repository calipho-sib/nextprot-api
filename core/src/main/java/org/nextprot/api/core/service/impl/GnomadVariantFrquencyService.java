package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.domain.annotation.*;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.VariantFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GnomadVariantFrquencyService implements VariantFrequencyService {

    @Autowired
    DbXrefService xrefService;

    private static final Log LOGGER = LogFactory.getLog(GnomadVariantFrquencyService.class);

    private Stack<String> logStack = new Stack<>();

    private Map<String, List<VariantFrequency>> findVariantFrequenciesByDBSNP(Set<String> DBSNPIds) {
        return null;
    }

    @Override
    public void addFrequencyEvidences(String entryName, List<Annotation> annotations) {
        addGnomeADVariantFrequencies(entryName, annotations);
    }

    // This method is written to match the gnomad variant data with existing DBSNP
    // This is a temporary workaround until the gnomad data is properly loaded
    private void addGnomeADVariantFrequencies(String entryName, List<Annotation> annotations) {
        double start = System.currentTimeMillis();
        LOGGER.info("Processing " + entryName + ": " + annotations.size() + " annotations");
        final ArrayList<String> logs = new ArrayList<>();
        logStack.push("LENTRY:" + entryName);

        // Get all the gnomeAd variants for all the variations with dbSNPIds
        Set<String> dbSNPIds = new HashSet<>();
        List<Annotation> variantAnnotations = annotations.stream()
                .filter(annotation -> AnnotationCategory.VARIANT.getDbAnnotationTypeName().equals(annotation.getCategory()))
                .map(annotation -> {
                    // Gets the annotation evidence referring to dbSNP
                    List<String> dbSNPIdsForAnnotation = annotation.getEvidences().stream()
                            .filter((annotationEvidence -> "dbSNP".equals(annotationEvidence.getResourceDb())))
                            .map(annotationEvidence -> annotationEvidence.getResourceAccession())
                            .collect(Collectors.toList());
                    dbSNPIds.addAll(dbSNPIdsForAnnotation);
                    return annotation;
                })
                .collect(Collectors.toList());
        LOGGER.info("DBSNPs to search for " + dbSNPIds.size());

        // Get all the gnomeAd variants given the dbSNPIds
        Map<Annotation, Set<AnnotationEvidence>> newEvidences = new HashMap<>();
        if (dbSNPIds.size() > 0) {
            Map<String, List<VariantFrequency>> variantFrequencies = findVariantFrequenciesByDBSNP(dbSNPIds);
            if (variantFrequencies == null) {
                LOGGER.info("No GNOMAD variants found for given dbsnpids " + dbSNPIds.toArray().toString());
            }

            variantAnnotations.stream()
                    .filter(annotation -> AnnotationCategory.VARIANT.getDbAnnotationTypeName().equals(annotation.getCategory()))
                    .forEach(annotation -> {
                        List<AnnotationEvidence> annotationEvidences = annotation.getEvidences();
                        logStack.push("LANNOT:" + annotation.getAnnotationId());
                        annotationEvidences.stream()
                                .filter((annotationEvidence -> "dbSNP".equals(annotationEvidence.getResourceDb())))
                                .forEach(annotationEvidence -> { // Match the annotations evidence with the gnomad frequencies
                                    String dbSNPId = annotationEvidence.getResourceAccession();
                                    logStack.push("LANNOTEV:" + dbSNPId);

                                    // Do the consistency checks before attaching the variant frequencies
                                    String annotationVariantOriginal = annotation.getVariant().getOriginal();
                                    String annotationVariantVariant = annotation.getVariant().getVariant();

                                    logStack.push("annotationvariantoriginalAA:" + annotation.getVariant().getOriginal());
                                    logStack.push("annotationvariantvariantAA:" + annotation.getVariant().getVariant());

                                    // Get variant frequency for this annotation
                                    List<VariantFrequency> variantFrequencyList = variantFrequencies.get(dbSNPId);
                                    if (variantFrequencyList != null) {
                                        variantFrequencyList.forEach(variantFrequency -> {
                                            logStack.push("LGNADVar:" + variantFrequencyList.size());

                                            String gnomeadOriginalAA = variantFrequency.getOriginalAminoAcid();
                                            String gnomeadVariantAA = variantFrequency.getVariantAminoAcid();

                                            // Gnomead variant amino acids are in three letter code, need to be converted
                                            String gnomeadOriginalAA1Letter = AminoAcidCode.valueOfAminoAcid(gnomeadOriginalAA).get1LetterCode();
                                            String gnomeadVariantAA1Letter = AminoAcidCode.valueOfAminoAcid(gnomeadVariantAA).get1LetterCode();

                                            logStack.push("gnomadaccession:" + variantFrequency.getGnomadAccession());
                                            logStack.push("gnomadoriginalAA:" + gnomeadOriginalAA1Letter);
                                            logStack.push("gnomadvariantAA:" + gnomeadVariantAA1Letter);
                                            logStack.push("gnomadvariantposition:" + variantFrequency.getIsoformPosition());

                                            // Check if the variant is the same
                                            // Tis is the check which has to make profound considering all/most of the possibilities
                                            annotation.getTargetingIsoformsMap()
                                                    .forEach((key, isoformSpecificity) -> {
                                                        logStack.push("LISOFORM:" + key);
                                                        logStack.push("isofirstpos:" + isoformSpecificity.getFirstPosition());
                                                        logStack.push("isolastpos:" + isoformSpecificity.getLastPosition());
                                                        if (isoformSpecificity.getFirstPosition().equals(isoformSpecificity.getLastPosition())) { // only consider this simple case for now
                                                            logStack.push("missense:true");
                                                            if (variantFrequency.getIsoformPosition() == isoformSpecificity.getFirstPosition()) {
                                                                logs.add("isPositionRange:false");
                                                                // Positions match
                                                                LOGGER.info("Variant position " + variantFrequency.getIsoformPosition() + " matches with  annotation isoform " + key);
                                                                logStack.push("VariantPositionMatch:true");

                                                                if (gnomeadOriginalAA1Letter.equals(annotationVariantOriginal)) {
                                                                    if (gnomeadVariantAA1Letter.equals(annotationVariantVariant)) {
                                                                        //LOGGER.info("GNOMAD variant matches with annotation variant for " + variantFrequency.getGnomadAccession() + " " + annotation.getAnnotationId());
                                                                        // Adds evidence
                                                                        AnnotationEvidence newAnnotationEvidence = createAnnotationEvidence(annotation, variantFrequency);

                                                                        // Adds the frequencies as a new evidence
                                                                        addFrequencyDetailsToEvidence(newAnnotationEvidence, variantFrequency);

                                                                        if (newEvidences.get(annotation) == null) {
                                                                            Set<AnnotationEvidence> evidenceList = new HashSet<>();
                                                                            evidenceList.add(newAnnotationEvidence);
                                                                            newEvidences.put(annotation, evidenceList);
                                                                        } else {
                                                                            newEvidences.get(annotation).add(annotationEvidence);
                                                                        }

                                                                        // Generates the flat log line for this particular case
                                                                        logStack.push("VariantAAOriginalMatch:true");
                                                                        logStack.push("VariantAAVariantMatch:true");
                                                                        logStack.push("CrossVariantMatch:false");
                                                                        logStack.push("VariantMatch:true");
                                                                        writeLog(logStack);
                                                                        popLogStack(logStack, "LISOFORM");

                                                                    } else {
                                                                        // variant amino acid sequence do not match
                                                                        logStack.push("VariantAAOriginalMatch:true");
                                                                        logStack.push("VariantAAVariantMatch:false");

                                                                        // Check for cross AA mathing
                                                                        // i.e original = variant and vice versa
                                                                        if (gnomeadOriginalAA.equals(annotationVariantVariant) && gnomeadVariantAA.equals(annotationVariantOriginal)) {
                                                                            logStack.push("CrossVariantMatch:true");
                                                                        }
                                                                    }
                                                                } else {
                                                                    logStack.push("VariantAAOriginalMatch:false");
                                                                    if (gnomeadVariantAA1Letter.equals(annotationVariantVariant)) {
                                                                        logStack.push("VariantAAVariantMatch:true");
                                                                    } else {
                                                                        logStack.push("VariantAAVariantMatch:false");
                                                                    }

                                                                    // Check for cross AA mathing
                                                                    // i.e original = variant and vice versa
                                                                    if (gnomeadOriginalAA.equals(annotationVariantVariant) && gnomeadVariantAA.equals(annotationVariantOriginal)) {
                                                                        logStack.push("CrossVariantMatch:true");
                                                                    }

                                                                    logStack.push("VariantMatch:false");

                                                                    // Check for cross AA matching
                                                                    // i.e original = variant and vice versa
                                                                    if (gnomeadOriginalAA.equals(annotationVariantVariant) && gnomeadVariantAA.equals(annotationVariantOriginal)) {

                                                                    }
                                                                    writeLog(logStack);
                                                                    popLogStack(logStack, "LISOFORM");
                                                                }
                                                            } else {
                                                                logStack.push("VariantPositionMatch:false");
                                                                // Check for cross AA mathing
                                                                // i.e original = variant and vice versa
                                                                if (gnomeadOriginalAA.equals(annotationVariantVariant) && gnomeadVariantAA.equals(annotationVariantOriginal)) {
                                                                    logStack.push("CrossVariantMatch:true");
                                                                }
                                                                logStack.push("VariantMatch:false");
                                                                writeLog(logStack);
                                                                popLogStack(logStack, "LISOFORM");
                                                            }
                                                        } else {
                                                            LOGGER.info("Annotation variant in a range " + isoformSpecificity.getFirstPosition() + " -> " + isoformSpecificity.getLastPosition());
                                                            logStack.push("missense:false");

                                                            if (gnomeadOriginalAA.equals(annotationVariantVariant) && gnomeadVariantAA.equals(annotationVariantOriginal)) {
                                                                logStack.push("CrossVariantMatch:true");
                                                            }
                                                            writeLog(logStack);
                                                            popLogStack(logStack, "LISOFORM");
                                                        }
                                                    });
                                            popLogStack(logStack, "LGNADVar");
                                        });
                                    } else {
                                        logStack.push("gnomadvariants:" + 0);
                                        writeLog(logStack);
                                    }
                                    popLogStack(logStack,"LANNOTEV");

                                });
                        popLogStack(logStack,"LANNOT");
                    });
        } else {
            logStack.push("DBSNPEvidence:0");
            writeLog(logStack);
            popLogStack(logStack,"LENTRY");
            LOGGER.info("No DBSNP ids for given annotations");
        }

        // Adds properties and evidences
        newEvidences.keySet().stream()
                .forEach((annotation -> {
                    // Filters out the duplicated evidences add for multiple isoforms
                    // Naive duplicate identification
                    List<AnnotationEvidence> annotationEvidences =new ArrayList<>();
                    newEvidences.get(annotation)
                            .forEach(annotationEvidence -> {
                                boolean exists = false;
                                for(AnnotationEvidence existingEvidence: annotationEvidences) {
                                    if(existingEvidence.getResourceAccession().equals(annotationEvidence.getResourceAccession())) {
                                        exists = true;
                                        break;
                                    }
                                }
                                if(!exists) {
                                    System.out.println("Adding evidence" + annotationEvidence.getResourceAccession());
                                    annotationEvidences.add(annotationEvidence);
                                }
                            });

                    // Sets the quality to GOLD
                    annotation.getEvidences().addAll(annotationEvidences);
                    annotation.setQualityQualifier("GOLD");
                }));
        double time = System.currentTimeMillis() - start;
        LOGGER.info("MatchedGnomADVariants:" + newEvidences.keySet().size());
        LOGGER.info("ElapsedTime:" + time);
    }


    /**
     * Create annotation evidence given the variant frequency
     * @param annotation
     * @param variantFrequency
     * @return AnnotationEvidence
     */
    private AnnotationEvidence createAnnotationEvidence(Annotation annotation, VariantFrequency variantFrequency) {
        AnnotationEvidence annotationEvidence = new AnnotationEvidence();
        long xrefId = -1;
        try {
            xrefId = xrefService.findXrefId("gnomAD", variantFrequency.getGnomadAccession());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Unable to create xref ID ");
        }
        if (xrefId != -1) {
            annotationEvidence.setResourceId(xrefId);
        } else {
            LOGGER.info("XREF could not be generated");
        }
        annotationEvidence.setEvidenceId(IdentifierOffset.EVIDENCE_ID_COUNTER_FOR_GNOMAD.incrementAndGet());
        annotationEvidence.setEvidenceCodeAC("ECO:0000219");
        annotationEvidence.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
        annotationEvidence.setEvidenceCodeName("nucleotide sequencing assay evidence");
        annotationEvidence.setAssignedBy("gnomAD");
        annotationEvidence.setResourceDb("gnomAD");
        annotationEvidence.setAnnotationId(annotation.getAnnotationId());
        annotationEvidence.setResourceAccession(variantFrequency.getGnomadAccession());
        annotationEvidence.setResourceAssociationType(annotationEvidence.getResourceAssociationType()); // Should this be changed?
        annotationEvidence.setResourceType("database"); // Should this be changed ?
        annotationEvidence.setQualityQualifier("GOLD");
        return annotationEvidence;
    }

    private AnnotationEvidenceProperty createAnnotationEvidenceProperty(AnnotationEvidence evidence, String propertyName, String propertyValue) {
        AnnotationEvidenceProperty annotationProperty = new AnnotationEvidenceProperty();
        annotationProperty.setEvidenceId(evidence.getEvidenceId());
        annotationProperty.setPropertyName(propertyName);
        annotationProperty.setPropertyValue(propertyValue);
        return annotationProperty;
    }

    private void addFrequencyDetailsToEvidence(AnnotationEvidence annotationEvidence, VariantFrequency variantFrequency) {
        // Adds allele frequency property and add it to the evidence
        Double alleleFrequency = new Double(variantFrequency.getAllelFrequency());
        AnnotationEvidenceProperty freqProperty = createAnnotationEvidenceProperty(annotationEvidence, "allele frequency", alleleFrequency.toString());

        // Adds allele count property
        Integer alleleCount = new Integer(variantFrequency.getAlleleCount());
        AnnotationEvidenceProperty countProperty = createAnnotationEvidenceProperty(annotationEvidence, "allele count", alleleCount.toString());

        // Adds allele number property
        Integer alleleNumber = new Integer(variantFrequency.getAllelNumber());
        AnnotationEvidenceProperty numberProperty = createAnnotationEvidenceProperty(annotationEvidence, "allele number", alleleNumber.toString());

        // Adds homozygote number property
        Integer homozygoteNumber = new Integer(variantFrequency.getHomozygoteCount());
        AnnotationEvidenceProperty homozegoteProperty = createAnnotationEvidenceProperty(annotationEvidence, "homozygote count", homozygoteNumber.toString());

        List<AnnotationEvidenceProperty> evidencePropertyList = new ArrayList<>();
        evidencePropertyList.add(freqProperty);
        evidencePropertyList.add(countProperty);
        evidencePropertyList.add(numberProperty);
        evidencePropertyList.add(homozegoteProperty);
        annotationEvidence.setProperties(evidencePropertyList);
    }

    private void writeLog(Stack<String> logStack) {
        String logString = logStack.stream()
                .collect(Collectors.joining(", ", "MATCHSTART ", " MATCHEND"));
        LOGGER.info(logString);
    }

    private void popLogStack(Stack<String> logStack, String parentLevel) {
        // Should pop until remove all logs for this isoform
        String logPop = "";
        do {
            logPop = logStack.pop();
        } while (!logPop.startsWith(parentLevel));
    }
}