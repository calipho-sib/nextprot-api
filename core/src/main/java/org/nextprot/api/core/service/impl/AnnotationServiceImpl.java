package org.nextprot.api.core.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.dao.BioPhyChemPropsDao;
import org.nextprot.api.core.dao.PtmDao;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.service.annotation.CatalyticActivityUtils;
import org.nextprot.api.core.service.annotation.PhenotypeUtils;
import org.nextprot.api.core.service.annotation.VariantUtils;
import org.nextprot.api.core.service.annotation.merge.impl.AnnotationListMerger;
import org.nextprot.api.core.utils.QuickAndDirtyKeywordProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nextprot.api.core.domain.Overview.EntityNameClass.GENE_NAMES;

@Service
public class AnnotationServiceImpl implements AnnotationService {

    private static final Log LOGGER = LogFactory.getLog(AnnotationServiceImpl.class);
    @Autowired
    private AnnotationDAO annotationDAO;
    @Autowired
    private MdataService mdataService;
    @Autowired
    private PtmDao ptmDao;
    @Autowired
    private DbXrefService xrefService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private BioPhyChemPropsDao bioPhyChemPropsDao;
    @Autowired
    private IsoformService isoformService;
    @Autowired
    private PeptideMappingService peptideMappingService;
    @Autowired
    private AntibodyMappingService antibodyMappingService;
    @Autowired
    private StatementService statementService;
    @Autowired
    private TerminologyService terminologyService;
    @Autowired
    private CvTermGraphService cvTermGraphService;
    @Autowired
    private ExperimentalContextDictionaryService experimentalContextDictionaryService;
    @Autowired
    private EntityNameService entityNameService;
    @Autowired
    private VariantFrequencyService variantFrequencyService;
    @Autowired
    private EntryPublicationService entryPublicationService;

    private static void refactorDescription(Annotation annotation) {

        String category = annotation.getCategory();

        if (annotation.getDescription() == null || annotation.getDescription().indexOf(':') == 1) {
            annotation.setDescription(annotation.getCvTermName());
        }

        if (category != null) {
            if ("sequence caution".equals(category)) {
                setSequenceCautionDescription(annotation);
            } else if ("go molecular function".equals(category) || "go cellular component".equals(category) || "go biological process".equals(category)) {
                setGODescription(annotation);
            } else if ("sequence conflict".equals(category) || "sequence variant".equals(category) || "mutagenesis site".equals(category)) {
                setVariantDescription(annotation);
            }
        }
    }

    private static void setSequenceCautionDescription(Annotation annotation) {

        SortedSet<String> acs = new TreeSet<>();

        for (AnnotationProperty ap : annotation.getProperties()) {
            if ("differing sequence".equals(ap.getName()))
                acs.add(ap.getAccession());
        }


        StringBuilder sb = new StringBuilder("The sequence").append(acs.size() > 1 ? "s" : "");
        for (String emblAc : acs) {
            sb.append(" ").append(emblAc);
        }
        sb.append(" differ").append(acs.size() == 1 ? "s" : "").append(" from that shown.");

        // Beginning of the sentence finish, then:
        List<AnnotationProperty> conflictTypeProps = new ArrayList<>();
        for (AnnotationProperty ap : annotation.getProperties()) {
            if ("conflict type".equals(ap.getName()))
                conflictTypeProps.add(ap);
        }

        SortedSet<AnnotationProperty> sortedPositions = getSortedPositions(annotation);
        if (conflictTypeProps != null && !conflictTypeProps.isEmpty()) {
            sb.append(" Reason:");
            for (AnnotationProperty prop : conflictTypeProps) {
                sb.append(" ").append(prop.getValue());
            }
            if (!sortedPositions.isEmpty()) {
                sb.append(" at position").append(sortedPositions.size() > 1 ? "s" : "");
                for (AnnotationProperty p : sortedPositions) {
                    sb.append(" ").append(p.getValue());
                }
            }
            sb.append(".");
        }
        if (StringUtils.isNotEmpty(annotation.getDescription())) {
            sb.append(" ").append(annotation.getDescription());
        }

        annotation.setDescription(sb.toString());
    }

    private static SortedSet<AnnotationProperty> getSortedPositions(Annotation annotation) {
        SortedSet<AnnotationProperty> sortedPositions = new TreeSet<>((p1, p2) -> Integer.valueOf(p1.getValue()).compareTo(Integer.valueOf(p2.getValue())));

        List<AnnotationProperty> conflictPositions = new ArrayList<>();
        for (AnnotationProperty ap : annotation.getProperties()) {
            if ("position".equals(ap.getName()))
                conflictPositions.add(ap);
        }

        if (conflictPositions != null && !conflictPositions.isEmpty()) {
            sortedPositions.addAll(conflictPositions);
        }
        return sortedPositions;
    }

    private static void setVariantDescription(Annotation annotation) {

        if (annotation.getVariant() != null && annotation.getVariant().getVariant().isEmpty()) {
            String description = annotation.getDescription();
            annotation.setDescription("Missing " + (description == null ? "" : description));
        }
    }

    private static void setGODescription(Annotation annotation) {

        //Example if the cv term is : nuclear proteasome complex and there is a GO term of go_qualifier, the description changes to:
        //Then the description Colocalizes with nuclear proteasome complex

        for (AnnotationEvidence evidence : annotation.getEvidences()) {
            if ("evidence".equals(evidence.getResourceAssociationType())) {
                String goqualifier = evidence.getGoQualifier();
                if (goqualifier != null && !goqualifier.isEmpty()) {
                    String description = StringUtils.capitalize(goqualifier.replaceAll("_", " ") + " ") + annotation.getDescription();
                    annotation.setDescription(description);
                    break;
                }
            }
        }
    }

    @Override
    @Cacheable(value = "annotations", sync = true)
    public List<Annotation> findAnnotations(String entryName) {
        return findAnnotations(entryName, false);
    }

    @Override
    public Map<String, List<Annotation>> findAnnotationsByCategory(String entryName, List<String> annotationCategories) {
        Set<String> annotationCategorySet = new HashSet<>(annotationCategories);

        List<Annotation> annotations = findAnnotations(entryName);
        Map<AnnotationCategory, List<Annotation>> groupedAnnotations = annotations
                .parallelStream()
                .filter(annotation -> annotationCategorySet.contains(annotation.getAPICategory().getApiTypeName()))
                .collect(Collectors.groupingBy(Annotation::getAPICategory));
        Map<String, List<Annotation>> annotationsByCategories = new HashMap<>();
        EntryPublications publications = entryPublicationService.findEntryPublications(entryName);
        List<DbXref> xrefs = xrefService.findDbXrefByAccession(entryName);


        for (AnnotationCategory cat : groupedAnnotations.keySet()) {
            annotationsByCategories.put(cat.getApiTypeName(), groupedAnnotations.get(cat));
        }

        return annotationsByCategories;
    }

    /**
     * pam: useful for test AnnotationServiceTest to work and for other tests
     */
    @Override
    public List<Annotation> findAnnotationsExcludingBed(String entryName) {
        return findAnnotations(entryName, true);
    }

    private List<Annotation> findAnnotations(String entryName, boolean ignoreStatements) {

        Preconditions.checkArgument(entryName != null, "The annotation name should be set wit #withName(...)");

        List<Annotation> annotations = annotationDAO.findAnnotationsByEntryName(entryName);
        if (!annotations.isEmpty()) {

            List<Long> annotationIds = Lists.transform(annotations, new AnnotationFunction());

            // Evidences
            List<AnnotationEvidence> evidences = annotationDAO.findAnnotationEvidencesByAnnotationIds(annotationIds);
            Multimap<Long, AnnotationEvidence> evidencesByAnnotationId = Multimaps.index(evidences, new AnnotationEvidenceFunction());
            for (Annotation annotation : annotations) {
                annotation.setEvidences(new ArrayList<>(evidencesByAnnotationId.get(annotation.getAnnotationId())));
            }

            // Evidences properties
            if (!evidences.isEmpty()) {
                List<Long> evidencesIds = Lists.transform(evidences, new AnnotationEvidenceIdFunction());
                List<AnnotationEvidenceProperty> evidenceProperties = annotationDAO.findAnnotationEvidencePropertiesByEvidenceIds(evidencesIds);
                Multimap<Long, AnnotationEvidenceProperty> propertiesByEvidenceId = Multimaps.index(evidenceProperties, new AnnotationEvidencePropertyFunction());
                for (AnnotationEvidence evidence : evidences) {
                    evidence.setProperties(new ArrayList<>(propertiesByEvidenceId.get(evidence.getEvidenceId())));
                }
            }

            // Isoform specificities
            List<AnnotationIsoformSpecificity> isospecs = annotationDAO.findAnnotationIsoformsByAnnotationIds(annotationIds);
            Multimap<Long, AnnotationIsoformSpecificity> isospecsByAnnotationId = Multimaps.index(isospecs, new AnnotationIsoformFunction());

            for (Annotation annotation : annotations) {
                annotation.addTargetingIsoforms(new ArrayList<>(isospecsByAnnotationId.get(annotation.getAnnotationId())));
            }

            // Properties
            List<AnnotationProperty> properties = annotationDAO.findAnnotationPropertiesByAnnotationIds(annotationIds);
            Multimap<Long, AnnotationProperty> propertiesByAnnotationId = Multimaps.index(properties, new AnnotationPropertyFunction());

            for (Annotation annotation : annotations) {
                annotation.addProperties(propertiesByAnnotationId.get(annotation.getAnnotationId()));
            }
            // Removes annotations which do not map to any isoform,
            // this may happen in case where the annotation has been seen in a peptide and the annotation was propagated to the master,
            // but we were not able to map to any isoform
            Iterator<Annotation> annotationsIt = annotations.iterator();
            while (annotationsIt.hasNext()) {
                Annotation a = annotationsIt.next();
                if (a.getTargetingIsoformsMap().size() == 0) {
                    annotationsIt.remove();
                }
            }


            AnnotationUtils.convertRelativeEvidencesToProperties(annotations); // CALIPHOMISC-277

            for (Annotation annot : annotations) {
                refactorDescription(annot);
            }
        }

        annotations.addAll(this.createSmallMoleculeInteractionAnnotationsFromCatalyticActivities(entryName, annotations, ignoreStatements));
        annotations.addAll(this.xrefService.findDbXrefsAsAnnotationsByEntry(entryName));
        annotations.addAll(this.interactionService.findInteractionsAsAnnotationsByEntry(entryName));
        annotations.addAll(this.peptideMappingService.findNaturalPeptideMappingAnnotationsByMasterUniqueName(entryName));
        annotations.addAll(this.peptideMappingService.findSyntheticPeptideMappingAnnotationsByMasterUniqueName(entryName));
        annotations.addAll(this.antibodyMappingService.findAntibodyMappingAnnotationsByUniqueName(entryName));
        annotations.addAll(bioPhyChemPropsToAnnotationList(entryName, this.bioPhyChemPropsDao.findPropertiesByUniqueName(entryName)));
        // Adds the variant frequencies to variant annotations
        addGnomeADVariantFrequencies(entryName, annotations);

        String geneName = entityNameService.findNamesByEntityNameClass(entryName, GENE_NAMES).stream()
                .filter(entityName -> entityName.isMain())
                .map(entityName -> entityName.getName())
                .findFirst()
                .orElse("");

        if (!ignoreStatements) {
            annotations = new AnnotationListMerger(geneName, annotations).merge(statementService.getAnnotations(entryName));
        }

        // post-processing of annotations
        updateIsoformsDisplayedAsSpecific(annotations, entryName);
        updateVariantsRelatedToDisease(annotations);
        updateVariantsHgvsNames(annotations, isoformService.findIsoformsByEntryName(entryName), geneName);
        updateVariantEvidences(annotations);
        updateSubcellularLocationTermNameWithAncestors(annotations);
        updateMiscRegionsRelatedToInteractions(annotations);
        updatePtmAndPeptideMappingWithMdata(annotations, entryName);
        updatePhenotypicEffectProperty(annotations, entryName);
        QuickAndDirtyKeywordProcessor.processKeywordAnnotations(annotations, entryName,
                isoformService.findIsoformsByEntryName(entryName), terminologyService);

        //returns a immutable list when the result is cache-able (this prevents modifying the cache, since the cache returns a reference)
        return new ImmutableList.Builder<Annotation>().addAll(annotations).build();
    }

    /**
     * Update evidences of variant annotations by filter out evidences from neXtProt WITHOUT experimental context
     * when there is another same evidence from neXtProt WITH an experimental context.
     */
    private void updateVariantEvidences(List<Annotation> annotations) {
        for (Annotation a : annotations) {
            if (AnnotationCategory.VARIANT.equals(a.getAPICategory())) {
                Set<AnnotationEvidence> similarNpEv = a.getEvidences().stream()
                        .filter(ev1 -> "NextProt".equals(ev1.getAssignedBy()))
                        .filter(ev1 -> a.getEvidences().stream().anyMatch(ev2 -> !ev1.equals(ev2) && equalsExceptEC(ev1, ev2)))
                        .collect(Collectors.toSet());
                if (similarNpEv.size() > 1) {
                    a.getEvidences().removeAll(similarNpEv.stream().filter(e -> e.getExperimentalContextId() == null).collect(Collectors.toSet()));
                }
            }
        }
    }

    private boolean equalsExceptEC(AnnotationEvidence ev1, AnnotationEvidence ev2) {
        return ev1.getResourceId() == ev2.getResourceId() &&
                ev1.isNegativeEvidence() == ev2.isNegativeEvidence() &&
                // ev1.getEvidenceId() == ev2.getEvidenceId() &&
                Objects.equals(ev1.getProperties(), ev2.getProperties()) &&
                Objects.equals(ev1.getResourceType(), ev2.getResourceType()) &&
                Objects.equals(ev1.getResourceAccession(), ev2.getResourceAccession()) &&
                Objects.equals(ev1.getResourceDb(), ev2.getResourceDb()) &&
                Objects.equals(ev1.getResourceDescription(), ev2.getResourceDescription()) &&
                // Objects.equals(ev1.experimentalContextId, ev2.experimentalContextId) &&
                Objects.equals(ev1.getMdataId(), ev2.getMdataId()) &&
                Objects.equals(ev1.getQualifierType(), ev2.getQualifierType()) &&
                Objects.equals(ev1.getQualityQualifier(), ev2.getQualityQualifier()) &&
                Objects.equals(ev1.getAssignedBy(), ev2.getAssignedBy()) &&
                Objects.equals(ev1.getGoAssignedBy(), ev2.getGoAssignedBy()) &&
                Objects.equals(ev1.getAssignmentMethod(), ev2.getAssignmentMethod()) &&
                Objects.equals(ev1.getEvidenceCodeAC(), ev2.getEvidenceCodeAC()) &&
                Objects.equals(ev1.getEvidenceCodeName(), ev2.getEvidenceCodeName()) &&
                Objects.equals(ev1.getEvidenceCodeOntology(), ev2.getEvidenceCodeOntology()) &&
                Objects.equals(ev1.getNote(), ev2.getNote()) &&
                Objects.equals(ev1.getResourceAssociationType(), ev2.getResourceAssociationType());
    }

    private void updatePhenotypicEffectProperty(List<Annotation> annotations, String entryName) {
        //System.out.println("I was there");
        PhenotypeUtils.updatePhenotypicEffectProperty(annotations, entryName);
    }

    private List<Annotation> createSmallMoleculeInteractionAnnotationsFromCatalyticActivities(String entryName, List<Annotation> existingAnnotations, boolean ignoreStatements) {
        List<Annotation> smiAnnotations = new ArrayList<>();
        List<DbXref> entryXrefs = ignoreStatements ? xrefService.findDbXrefsByMasterExcludingBed(entryName) : xrefService.findDbXrefsByMaster(entryName);
        List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);
        for (Annotation annot : existingAnnotations) {
            if (AnnotationCategory.CATALYTIC_ACTIVITY == annot.getAPICategory()) {
                smiAnnotations.addAll(
                        CatalyticActivityUtils.createSMIAnnotations(
                                entryName, isoforms, annot, entryXrefs));
            }
        }
        List<Annotation> mergedSmiAnnotations = CatalyticActivityUtils.mergeSmiAnnotations(smiAnnotations);
        return mergedSmiAnnotations;
    }

    private void updateSubcellularLocationTermNameWithAncestors(List<Annotation> annotations) {

        //long t0 = System.currentTimeMillis(); System.out.println("updateSubcellularLocationTermNameWithAncestors...");

        for (Annotation annot : annotations) {
            if (AnnotationCategory.SUBCELLULAR_LOCATION == annot.getAPICategory()) {
                CvTerm t = terminologyService.findCvTermByAccessionOrThrowRuntimeException(annot.getCvTermAccessionCode());
                List<CvTerm> terms = terminologyService.getOnePathToRootTerm(t.getAccession());
                String longName = AnnotationUtils.getTermNameWithAncestors(annot, terms);
                AnnotationProperty prop = new AnnotationProperty();
                prop.setAnnotationId(annot.getAnnotationId());
                prop.setName("long-name");
                prop.setValue(String.valueOf(longName));
                annot.addProperty(prop);
                String descr = annot.getDescription();
                if (descr != null && !annot.getCvTermName().equals(descr)) {
                    // 3 cases: "Main location", "Additional location" or "Note=..."
                    if (descr.startsWith("Note=")) descr = descr.substring(5);
                    prop = new AnnotationProperty();
                    prop.setAnnotationId(annot.getAnnotationId());
                    prop.setName("name-modifier");
                    prop.setValue(String.valueOf(descr));
                    annot.addProperty(prop);
                }
            }
        }

        //System.out.println("updateSubcellularLocationTermNameWithAncestors DONE in " + (System.currentTimeMillis() - t0) + "ms");

    }

    private void updateVariantsHgvsNames(List<Annotation> annotations, List<Isoform> isoforms, String geneName) {

        for (Annotation a : annotations) {
            if (a.getAPICategory() == null)
                continue; // this should not accur but it DOES occur in a mockito test i don't understand
            if (a.getAPICategory().equals(AnnotationCategory.VARIANT) ||
                    a.getAPICategory().equals(AnnotationCategory.MUTAGENESIS)) {
                for (Isoform iso : isoforms) VariantUtils.updateHGVSName(a, iso, geneName);
            }
        }
    }


    private void updateVariantsRelatedToDisease(List<Annotation> annotations) {

        Map<Long, ExperimentalContext> ecMap = experimentalContextDictionaryService.getIdExperimentalContextMap();

        //long t0 = System.currentTimeMillis(); System.out.println("updateVariantsRelatedToDisease...");

        Set<String> diseaseRelatedVariants = new HashSet<>();
        for (Annotation a : annotations) {
            if (a.getAPICategory() == null || a.getAPICategory().getDbAnnotationTypeName() == null
                    || !a.getAPICategory().getDbAnnotationTypeName().equals(AnnotationCategory.DISEASE_RELATED_VARIANT.getDbAnnotationTypeName())) {
                continue;
            }
            // Get all subjects of DISEASE_RELATED_VARIANT annotations
            diseaseRelatedVariants.addAll(a.getSubjectComponents());
        }
        // add property if annotation is a variant related to disease
        for (Annotation annot : annotations) {
            if (AnnotationCategory.VARIANT == annot.getAPICategory()) {
                boolean result = AnnotationUtils.isVariantRelatedToDiseaseProperty(annot, ecMap, diseaseRelatedVariants);
                AnnotationProperty prop = new AnnotationProperty();
                prop.setAnnotationId(annot.getAnnotationId());
                prop.setName("disease-related");
                prop.setValue(String.valueOf(result));
                annot.addProperty(prop);
            }
        }

        //System.out.println("updateVariantsRelatedToDisease DONE in " + (System.currentTimeMillis() - t0) + "ms");

    }

    private void updatePtmAndPeptideMappingWithMdata(List<Annotation> annotations, String entryName) {

        Map<Long, Long> evidenceMdataMap = mdataService.findEvidenceIdMdataIdMapByEntryName(entryName);
        annotations.stream().filter(a -> isAnnotationWithPotentialMdata(a)).forEach(a -> {
            a.getEvidences().forEach(e -> {
                e.setMdataId(evidenceMdataMap.get(e.getEvidenceId()));
            });
        });
    }

    private boolean isAnnotationWithPotentialMdata(Annotation a) {
        if (a.getAPICategory() == AnnotationCategory.PEPTIDE_MAPPING) return true;
        if (a.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE) return true;
        if (a.getAPICategory() == AnnotationCategory.GLYCOSYLATION_SITE) return true;
        if (a.getAPICategory() == AnnotationCategory.CROSS_LINK) return true;
        return false;
    }

    private void updateMiscRegionsRelatedToInteractions(List<Annotation> annotations) {

        // add property if annotation is a misc region and it is related to interaction
        for (Annotation annot : annotations) {
            if (AnnotationCategory.MISCELLANEOUS_REGION == annot.getAPICategory()) {
                boolean result = AnnotationUtils.isMiscRegionRelatedToInteractions(annot);
                AnnotationProperty prop = new AnnotationProperty();
                prop.setAnnotationId(annot.getAnnotationId());
                prop.setName("interaction-related");
                prop.setValue(String.valueOf(result));
                annot.addProperty(prop);
            }
        }
    }

    private void updateIsoformsDisplayedAsSpecific(List<Annotation> annotations, String entryName) {

        //long t0 = System.currentTimeMillis(); System.out.println("updateIsoformsDisplayedAsSpecific...");

        List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);
        int entryIsoformCount = isoforms.size();
        for (Annotation annot : annotations) {
            annot.setIsoformsDisplayedAsSpecific(AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot, entryIsoformCount));
        }
        //System.out.println("updateIsoformsDisplayedAsSpecific DONE in " + (System.currentTimeMillis() - t0) + "ms");

    }

    private List<Annotation> bioPhyChemPropsToAnnotationList(String entryName, List<AnnotationProperty> props) {

        List<Annotation> annotations = new ArrayList<>(props.size());

        List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);

        for (AnnotationProperty property : props) {

            Annotation annotation = new Annotation();

            AnnotationCategory model = AnnotationCategory.getByDbAnnotationTypeName(property.getName());
            String description = property.getValue();


            annotation.setAnnotationId(property.getAnnotationId() + IdentifierOffset.BIOPHYSICOCHEMICAL_ANNOTATION_OFFSET);
            annotation.setCategory(model.getDbAnnotationTypeName());
            annotation.setDescription(description);
            annotation.setEvidences(new ArrayList<>());

            annotation.setQualityQualifier("GOLD");
            annotation.setUniqueName(entryName + "_" + model.getApiTypeName());
            annotation.addTargetingIsoforms(newAnnotationIsoformSpecificityList(annotation.getAnnotationId(), isoforms));

            annotations.add(annotation);
        }

        return annotations;
    }

    private List<AnnotationIsoformSpecificity> newAnnotationIsoformSpecificityList(long annotationId, List<Isoform> isoforms) {

        List<AnnotationIsoformSpecificity> specs = new ArrayList<>(isoforms.size());

        for (Isoform isoform : isoforms) {

            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();

            spec.setAnnotationId(annotationId);
            spec.setIsoformAccession(isoform.getIsoformAccession());
            spec.setSpecificity("UNKNOWN");

            specs.add(spec);
        }

        return specs;
    }

    // This method is written to match the gnomad variant data with existing DBSNP
    // This is a temporary workaround until the gnomad data is properly loaded
    private void addGnomeADVariantFrequencies(String entryName, List<Annotation> annotations) {
        double start = System.currentTimeMillis();
        LOGGER.debug("Processing " + entryName + ": " + annotations.size() + " annotations");
        final ArrayList<String> logs = new ArrayList<>();
        Stack<String> logStack = new Stack<>();
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
        LOGGER.debug("DBSNPs to search for " + dbSNPIds.size());

        // Get all the gnomeAd variants given the dbSNPIds
        Map<Annotation, Set<AnnotationEvidence>> newEvidences = new HashMap<>();
        if (dbSNPIds.size() > 0) {
            Map<String, List<VariantFrequency>> variantFrequencies = variantFrequencyService.findVariantFrequenciesByDBSNP(dbSNPIds);
            if (variantFrequencies == null) {
                LOGGER.debug("No GNOMAD variants found for given dbsnpids " + dbSNPIds.toArray().toString());
            }

            variantAnnotations.stream()
                    .filter(annotation -> AnnotationCategory.VARIANT.getDbAnnotationTypeName().equals(annotation.getCategory()))
                    .forEach(annotation -> {
                        List<AnnotationEvidence> annotationEvidences = annotation.getEvidences();
                        logStack.push("LANNOT:" + annotation.getAnnotationId());
                        annotationEvidences.stream()
                                .filter((annotationEvidence -> "dbSNP".equals(annotationEvidence.getResourceDb())))
                                .forEach(annotationEvidence -> {
                                    String dbSNPId = annotationEvidence.getResourceAccession();
                                    logStack.push("LANNOTEV:" + dbSNPId);

                                    // Do the consistency checks before attaching the variant frequencies
                                    String annotationVariantOriginal = annotation.getVariant().getOriginal();
                                    String annotationVariantVariant = annotation.getVariant().getVariant();

                                    logStack.push("annotationvariantoriginalAA:" + annotation.getVariant().getOriginal());
                                    logStack.push("annotationvariantvariantAA:" + annotation.getVariant().getVariant());
                                    Map<String, AnnotationIsoformSpecificity> isoformMap = annotation.getTargetingIsoformsMap();

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
                                                            logs.add("isPositionRange:false");
                                                            // Positions match
                                                            LOGGER.debug("Variant position " + variantFrequency.getIsoformPosition() + " matches with  annotation isoform " + key);
                                                            logStack.push("VariantPositionMatch:true");

                                                            if (gnomeadOriginalAA1Letter.equals(annotationVariantOriginal)) {
                                                                if (gnomeadVariantAA1Letter.equals(annotationVariantVariant)) {
                                                                    //LOGGER.info("GNOMAD variant matches with annotation variant for " + variantFrequency.getGnomadAccession() + " " + annotation.getAnnotationId());
                                                                    // Adds evidence
                                                                    AnnotationEvidence gnomadEvidence = new AnnotationEvidence();
                                                                    long xrefId = -1;
                                                                    try {
                                                                        xrefId = xrefService.findXrefId("gnomAD", variantFrequency.getGnomadAccession());
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                        LOGGER.error("Unable to create xref ID ");
                                                                    }
                                                                    if (xrefId != -1) {
                                                                        gnomadEvidence.setResourceId(xrefId);
                                                                    } else {
                                                                        LOGGER.error("XREF could not be generated");
                                                                    }
                                                                    gnomadEvidence.setEvidenceId(IdentifierOffset.EVIDENCE_ID_COUNTER_FOR_GNOMAD.incrementAndGet());
                                                                    gnomadEvidence.setEvidenceCodeAC("ECO:0000219");
                                                                    gnomadEvidence.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
                                                                    gnomadEvidence.setEvidenceCodeName("nucleotide sequencing assay evidence");
                                                                    gnomadEvidence.setAssignedBy("gnomAD");
                                                                    gnomadEvidence.setResourceDb("gnomAD");
                                                                    gnomadEvidence.setAnnotationId(annotation.getAnnotationId());
                                                                    gnomadEvidence.setResourceAccession(variantFrequency.getGnomadAccession());
                                                                    gnomadEvidence.setResourceAssociationType(annotationEvidence.getResourceAssociationType()); // Should this be changed?
                                                                    gnomadEvidence.setResourceType("database"); // Should this be changed ?
                                                                    gnomadEvidence.setQualityQualifier("GOLD");

                                                                    // Adds allele frequency property and add it to the evidence
                                                                    AnnotationEvidenceProperty freqProperty = new AnnotationEvidenceProperty();
                                                                    freqProperty.setEvidenceId(gnomadEvidence.getEvidenceId());
                                                                    freqProperty.setPropertyName("allele frequency");
                                                                    freqProperty.setPropertyValue(new Double(variantFrequency.getAllelFrequency()).toString());

                                                                    // Adds allele count property
                                                                    AnnotationEvidenceProperty countProperty = new AnnotationEvidenceProperty();
                                                                    countProperty.setEvidenceId(gnomadEvidence.getEvidenceId());
                                                                    countProperty.setPropertyName("allele count");
                                                                    countProperty.setPropertyValue(new Integer(variantFrequency.getAlleleCount()).toString());

                                                                    // Adds allele count property
                                                                    AnnotationEvidenceProperty numberProperty = new AnnotationEvidenceProperty();
                                                                    numberProperty.setEvidenceId(gnomadEvidence.getEvidenceId());
                                                                    numberProperty.setPropertyName("allele number");
                                                                    numberProperty.setPropertyValue(new Integer(variantFrequency.getAllelNumber()).toString());

                                                                    // Adds allele count property
                                                                    AnnotationEvidenceProperty homozegoteNumber = new AnnotationEvidenceProperty();
                                                                    homozegoteNumber.setEvidenceId(gnomadEvidence.getEvidenceId());
                                                                    homozegoteNumber.setPropertyName("homozygote count");
                                                                    homozegoteNumber.setPropertyValue(new Integer(variantFrequency.getHomozygoteCount()).toString());

                                                                    List<AnnotationEvidenceProperty> evidencePropertyList = new ArrayList<>();
                                                                    evidencePropertyList.add(freqProperty);
                                                                    evidencePropertyList.add(countProperty);
                                                                    evidencePropertyList.add(numberProperty);
                                                                    evidencePropertyList.add(homozegoteNumber);
                                                                    gnomadEvidence.setProperties(evidencePropertyList);

                                                                    if (newEvidences.get(annotation) == null) {
                                                                        Set<AnnotationEvidence> evidenceList = new HashSet<>();
                                                                        evidenceList.add(gnomadEvidence);
                                                                        newEvidences.put(annotation, evidenceList);
                                                                    } else {
                                                                        newEvidences.get(annotation).add(gnomadEvidence);
                                                                    }

                                                                    // Generates the flat log line for this particular case
                                                                    logStack.push("VariantAAOriginalMatch:true");
                                                                    logStack.push("VariantAAVariantMatch:true");
                                                                    logStack.push("CrossVariantMatch:false");
                                                                    logStack.push("VariantMatch:true");

                                                                    String logString = logStack.stream()
                                                                            .collect(Collectors.joining(", ", "MATCHSTART ", " MATCHEND"));
                                                                    LOGGER.debug(logString);
                                                                    // Should pop until remove all logs for this isoform
                                                                    String logPop = "";
                                                                    do {
                                                                        logPop = logStack.pop();
                                                                    } while (!logPop.startsWith("LISOFORM"));

                                                                } else {
                                                                    // variant amino acid sequence do not match
                                                                    logStack.push("VariantAAOriginalMatch:true");
                                                                    logStack.push("VariantAAVariantMatch:false");

                                                                    // Check for cross AA mathing
                                                                    // i.e original = variant and vice versa
                                                                    if (gnomeadOriginalAA.equals(annotationVariantVariant) && gnomeadVariantAA.equals(annotationVariantOriginal)) {
                                                                        logStack.push("CrossVariantMatch:true");
                                                                    }

                                                                    logStack.push("VariantMatch:false");
                                                                    String logString = logStack.stream()
                                                                            .collect(Collectors.joining(", ", "MATCHSTART ", " MATCHEND"));
                                                                    LOGGER.debug(logString);
                                                                    // Should pop until remove all logs for this isoform
                                                                    String logPop = "";
                                                                    do {
                                                                        logPop = logStack.pop();

                                                                    } while (!logPop.startsWith("LISOFORM"));
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
                                                                String logString = logStack.stream()
                                                                        .collect(Collectors.joining(", ", "MATCHSTART ", " MATCHEND"));
                                                                LOGGER.debug(logString);
                                                                // Should pop until remove all logs for this isoform
                                                                String logPop = "";
                                                                do {
                                                                    logPop = logStack.pop();

                                                                } while (!logPop.startsWith("LISOFORM"));
                                                            }

                                                        } else {
                                                            LOGGER.debug("Annotation variant in a range " + isoformSpecificity.getFirstPosition() + " -> " + isoformSpecificity.getLastPosition());
                                                            logStack.push("missense:false");

                                                            if (gnomeadOriginalAA.equals(annotationVariantVariant) && gnomeadVariantAA.equals(annotationVariantOriginal)) {
                                                                logStack.push("CrossVariantMatch:true");
                                                            }
                                                            String logString = logStack.stream()
                                                                    .collect(Collectors.joining(", ", "MATCHSTART ", " MATCHEND"));
                                                            LOGGER.debug(logString);
                                                            // Should pop until remove all logs for this isoform
                                                            String logPop = "";
                                                            do {
                                                                logPop = logStack.pop();
                                                            } while (!logPop.startsWith("LISOFORM"));
                                                        }
                                                    });
                                            String logPop = "";
                                            do {
                                                logPop = logStack.pop();
                                            } while (!logPop.startsWith("LGNADVar"));
                                        });
                                    } else {
                                        logStack.push("gnomadvariants:" + 0);
                                        String logString = logStack.stream()
                                                .collect(Collectors.joining(", ", "MATCHSTART ", " MATCHEND"));
                                        LOGGER.debug(logString);
                                    }
                                    String logPop = "";
                                    do {
                                        logPop = logStack.pop();
                                    } while (!logPop.startsWith("LANNOTEV"));
                                });
                        String logPop = "";
                        do {
                            logPop = logStack.pop();
                        } while (!logPop.startsWith("LANNOT"));
                    });
        } else {
            logStack.push("DBSNPEvidence:0");
            String logPop = "";
            do {
                logPop = logStack.pop();
                LOGGER.debug(logPop);
            } while (!logPop.startsWith("LENTRY"));
            LOGGER.debug("No DBSNP ids for given annotations");
        }

        // Adds properties and evidences
        newEvidences.keySet().stream()
                .forEach((annotation -> {
                    // Filters out the duplicated evidences add for multiple isoforms
                    // Naive duplicate identification
                    List<AnnotationEvidence> annotationEvidences = new ArrayList<>();
                    newEvidences.get(annotation)
                            .forEach(annotationEvidence -> {
                                boolean exists = false;
                                for (AnnotationEvidence existingEvidence : annotationEvidences) {
                                    if (existingEvidence.getResourceAccession().equals(annotationEvidence.getResourceAccession())) {
                                        exists = true;
                                        break;
                                    }
                                }
                                if (!exists) {
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

    @Override
    public List<Feature> findPtmsByMaster(String uniqueName) {
        return this.ptmDao.findPtmsByEntry(uniqueName);
    }


    // Function class to filter using guava ///////////////////////////////////////////////////////////////////////////////

    @Override
    public List<Feature> findPtmsByIsoform(String isoformUniqueName) {
        String masterUniqueName = extractMasterUniqueName(isoformUniqueName);
        List<Feature> ptms = this.ptmDao.findPtmsByEntry(masterUniqueName);
        return filterByIsoform(isoformUniqueName, ptms);
    }

    @Override
    public Predicate<Annotation> createDescendantTermPredicate(String ancestorAccession) {

        return new BaseCvTermAncestorPredicate<Annotation>(terminologyService.findCvTermByAccessionOrThrowRuntimeException(ancestorAccession)) {

            @Override
            public boolean test(Annotation annotation) {
                try {
                    return annotation.getCvTermAccessionCode() != null &&
                            (annotation.getCvTermAccessionCode().equals(ancestor.getAccession()) ||
                                    graph.isAncestorOf(ancestor.getId().intValue(), graph.getCvTermIdByAccession(annotation.getCvTermAccessionCode())));
                } catch (CvTermGraph.NotFoundNodeException e) {
                    return false;
                }
            }
        };
    }

    @Override
    public Predicate<AnnotationEvidence> createDescendantEvidenceTermPredicate(String ancestorEvidenceCode) {

        return new BaseCvTermAncestorPredicate<AnnotationEvidence>(terminologyService.findCvTermByAccessionOrThrowRuntimeException(ancestorEvidenceCode)) {

            @Override
            public boolean test(AnnotationEvidence annotationEvidence) {

                try {
                    return annotationEvidence.getEvidenceCodeAC() != null &&
                            (annotationEvidence.getEvidenceCodeAC().equals(ancestor.getAccession()) ||
                                    graph.isAncestorOf(ancestor.getId().intValue(), graph.getCvTermIdByAccession(annotationEvidence.getEvidenceCodeAC())));
                } catch (CvTermGraph.NotFoundNodeException e) {
                    return false;
                }
            }
        };
    }

    @Override
    public Predicate<Annotation> buildPropertyPredicate(String propertyName, @Nullable String propertyValueOrAccession) {

        if (propertyName != null && !propertyName.isEmpty()) {

            Predicate<Annotation> propExistencePredicate = annotation -> annotation.getPropertiesMap().containsKey(propertyName);

            if (propertyValueOrAccession != null && !propertyValueOrAccession.isEmpty()) {

                return propExistencePredicate.and(annotation -> {

                    Collection<AnnotationProperty> props = annotation.getPropertiesByKey(propertyName);

                    return props.stream().anyMatch(annotationProperty ->
                            propertyValueOrAccession.equals(annotationProperty.getValue()) ||
                                    propertyValueOrAccession.equals(annotationProperty.getAccession()));
                });
            }
            return propExistencePredicate;
        }

        return annotation -> true;
    }

    private List<Feature> filterByIsoform(String isoformUniqueName, List<Feature> annotations) {
        List<Feature> filteredFeatures = new ArrayList<>();
        for (Feature f : annotations) {
            if (f.getIsoformAccession().equalsIgnoreCase(isoformUniqueName)) {
                filteredFeatures.add(f);
            }
        }
        return filteredFeatures;
    }

    private String extractMasterUniqueName(String isoformUniqueName) {
        if (isoformUniqueName.indexOf('-') < 1) {
            throw new InvalidParameterException(String.format(
                    "Invalid isoform accession [%s]", isoformUniqueName));
        }
        return isoformUniqueName.substring(0, isoformUniqueName.indexOf('-'));
    }

    // Refactor the descriptions  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private abstract class BaseCvTermAncestorPredicate<T> implements Predicate<T> {

        protected final CvTerm ancestor;
        protected final CvTermGraph graph;

        private BaseCvTermAncestorPredicate(CvTerm ancestor) {

            this.ancestor = ancestor;
            graph = cvTermGraphService.findCvTermGraph(TerminologyCv.valueOf(ancestor.getOntology()));
        }
    }

    private class AnnotationPropertyFunction implements Function<AnnotationProperty, Long> {
        public Long apply(AnnotationProperty annotation) {
            return annotation.getAnnotationId();
        }
    }

    private class AnnotationIsoformFunction implements Function<AnnotationIsoformSpecificity, Long> {
        public Long apply(AnnotationIsoformSpecificity annotation) {
            return annotation.getAnnotationId();
        }
    }

    private class AnnotationEvidenceFunction implements Function<AnnotationEvidence, Long> {
        public Long apply(AnnotationEvidence annotation) {
            return annotation.getAnnotationId();
        }
    }

    private class AnnotationEvidencePropertyFunction implements Function<AnnotationEvidenceProperty, Long> {
        public Long apply(AnnotationEvidenceProperty property) {
            return property.getEvidenceId();
        }
    }

    private class AnnotationFunction implements Function<Annotation, Long> {
        public Long apply(Annotation annotation) {
            return annotation.getAnnotationId();
        }
    }

    private class AnnotationEvidenceIdFunction implements Function<AnnotationEvidence, Long> {
        public Long apply(AnnotationEvidence evidence) {
            return evidence.getEvidenceId();
        }
    }
}
