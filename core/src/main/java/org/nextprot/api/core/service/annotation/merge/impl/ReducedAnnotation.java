package org.nextprot.api.core.service.annotation.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.annotation.merge.AnnotationDescriptionCombiner;
import org.nextprot.api.core.service.annotation.merge.AnnotationListReduction;
import org.nextprot.api.core.service.annotation.merge.SimilarGroupBuilder;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Merge annotations by updating and returning target annotation with source annotations
 */
public class ReducedAnnotation implements AnnotationListReduction {

    private static final Logger LOGGER = Logger.getLogger(ReducedAnnotation.class.getName());

    private final List<Annotation> annotations;
    private final Annotation destAnnotation;
    private final List<Annotation> sourceAnnotations;

    public ReducedAnnotation(SimilarGroupBuilder.SimilarAnnotationGroup annotationGroup) {

        Preconditions.checkNotNull(annotationGroup);
        Preconditions.checkArgument(!annotationGroup.isEmpty());

        this.annotations = annotationGroup.getAnnotations();

        if (annotations.size() == 1) {
            this.destAnnotation = annotations.get(0);
            this.sourceAnnotations = new ArrayList<>();
        } else {
            DestAnnotAndOtherSources destAnnotAndOtherSources = splitAnnotations(annotations);
            this.destAnnotation = destAnnotAndOtherSources.getDestAnnotation();
            this.sourceAnnotations = destAnnotAndOtherSources.getSourceAnnotations();
        }
    }

    /**
     * Find annotation coming from nextprot db
     */
    private static DestAnnotAndOtherSources splitAnnotations(List<Annotation> annotations) {

        int[] npAnnotIndices = IntStream.range(0, annotations.size())
                .filter(i -> annotations.get(i).getUniqueName() != null && annotations.get(i).getUniqueName().startsWith("AN_"))
                .toArray();

        if (npAnnotIndices.length == 0) {
            return new DestAnnotAndOtherSources(annotations.get(0), annotations.subList(1, annotations.size()));
        } else if (npAnnotIndices.length == 1) {
            return new DestAnnotAndOtherSources(annotations.get(npAnnotIndices[0]), annotations.stream()
                    .filter(annotation -> !annotation.getUniqueName().startsWith("AN_"))
                    .collect(Collectors.toList()));
        }
        throw new NextProtException("Multiple neXtProt annotations error: indices=" + Arrays.toString(npAnnotIndices)
                + ", annots=" + annotations);
    }

    @Override
    public Annotation reduce() {

        if (sourceAnnotations.isEmpty()) {

            return destAnnotation;
        }
        // Assertion: multiple annotations to merge

        updateDestEvidences();
        if (destAnnotation.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE) {
            updateDestDescription();
        }
        updateDestAnnotationHash();
        updateDestIsoformSpecificityName();
        updateDestQualityQualifier();
        updateDestBioObject();

        return destAnnotation;
    }

    /**
     * Update dest evidences with sources evidences
     */
    private void updateDestEvidences() {

        List<AnnotationEvidence> all = new ArrayList<>(destAnnotation.getEvidences());

        // TODO: to test
        for (Annotation other : sourceAnnotations) {

            all.addAll(other.getEvidences().stream()
                    .filter(e -> !destAnnotation.getEvidences().contains(e))
                    .collect(Collectors.toList()));
        }

        destAnnotation.setEvidences(all);
    }

    /**
     * Update dest description with source
     */
    private void updateDestDescription() {

        AnnotationDescriptionCombiner annotationDescriptionCombiner = new AnnotationDescriptionCombiner(destAnnotation);

        for (Annotation sourceAnnotation : sourceAnnotations) {

            destAnnotation.setDescription(annotationDescriptionCombiner.combine(destAnnotation.getDescription(), sourceAnnotation.getDescription()));
        }
    }

    /**
     * Update dest annotation hash
     */
    private void updateDestAnnotationHash() {

        if (destAnnotation.getAnnotationHash() == null) {

            Annotation firstSource = sourceAnnotations.get(0);

            String annotationHash = firstSource.getAnnotationHash();

            if (annotationHash == null || annotationHash.isEmpty())
                throw new NextProtException("annotation hash was not computed for source " + firstSource.getUniqueName());

            destAnnotation.setAnnotationHash(annotationHash);
        }
    }

    /**
     * Update dest isoform specificity name (variant name)
     */
    private void updateDestIsoformSpecificityName() {

        Map<String, AnnotationIsoformSpecificity> destTargetingIsoMap = destAnnotation.getTargetingIsoformsMap();

        // all sources shared the same isoform mapping
        Annotation firstSource = sourceAnnotations.get(0);

        for (Map.Entry<String, AnnotationIsoformSpecificity> sourceIsoformSpecificityEntry : firstSource.getTargetingIsoformsMap().entrySet()) {

            String isoformName = sourceIsoformSpecificityEntry.getKey();

            if (destTargetingIsoMap.containsKey(isoformName)) {

                if (doAnnotationIsoformSpecificitiesMergeable(destTargetingIsoMap.get(isoformName),
                        sourceIsoformSpecificityEntry.getValue(), destAnnotation.getAPICategory())) {

                    mergeAnnotationIsoformSpecificities(destTargetingIsoMap.get(isoformName), sourceIsoformSpecificityEntry.getValue());
                }
            } else {
                destTargetingIsoMap.put(isoformName, sourceIsoformSpecificityEntry.getValue());
            }
        }
    }

    /**
     * Reset dest qualityqualifier to gold if there is at least one gold source
     */
    private void updateDestQualityQualifier() {

        if (destAnnotation.getQualityQualifier() == null || QualityQualifier.valueOf(destAnnotation.getQualityQualifier()) != QualityQualifier.GOLD) {

            sourceAnnotations.stream()
                    .filter(annotation -> annotation.getQualityQualifier() != null && annotation.getQualityQualifier().equals(QualityQualifier.GOLD.name()))
                    .findFirst()
                    .ifPresent(a -> destAnnotation.setQualityQualifier(QualityQualifier.GOLD.name()));
        }
    }

    private void updateDestBioObject() {

        BioObject destBioObject = destAnnotation.getBioObject();

        for (Annotation source : sourceAnnotations) {

            BioObject srcBioObject = source.getBioObject();

            if (srcBioObject != null && srcBioObject.getProperties() != null) {
                updateDestBioObjectProperties(destBioObject, srcBioObject.getProperties());
            }
        }
    }

    private void updateDestBioObjectProperties(BioObject destBioObject, Map<String, String> srcBioObjectProperties) {

        Map<String, String> destProperties = destBioObject.getProperties();

        for (Map.Entry<String, String> srcKeyValue : srcBioObjectProperties.entrySet()) {

            if (!destProperties.containsKey(srcKeyValue.getKey())) {
                destBioObject.putPropertyNameValue(srcKeyValue.getKey(), srcKeyValue.getValue());
            } else if (!srcKeyValue.getValue().equals(destBioObject.getPropertyValue(srcKeyValue.getKey()))) {
                LOGGER.severe("Could not reset BioObject property '" + srcKeyValue.getKey() + "' for isoform "+ destBioObject.getAccession() +
                        ", keeping value '" + srcKeyValue.getValue() + "' (ignoring src value '" + destBioObject.getPropertyValue(srcKeyValue.getKey()) + "')");
            }
        }
    }

    private boolean doAnnotationIsoformSpecificitiesMergeable(AnnotationIsoformSpecificity dest, AnnotationIsoformSpecificity source, AnnotationCategory category) {

        boolean mergeable = true;

        if (!Objects.equals(source.getFirstPosition(), dest.getFirstPosition())) {
            LOGGER.severe("Category " + category + ": different first pos dest=" + dest.getIsoformAccession() + ", src=" + source.getIsoformAccession() + ", pos: src=" + source.getFirstPosition() + ", dest=" + dest.getFirstPosition());
            mergeable = false;
        }
        if (!Objects.equals(source.getLastPosition(), dest.getLastPosition())) {
            LOGGER.severe("Category " + category + ": different last pos dest=" + dest.getIsoformAccession() + ", src=" + source.getIsoformAccession() + ", pos: src=" + source.getLastPosition() + ", dest=" + dest.getLastPosition());
            mergeable = false;
        }
        // Mergeable anyway
        if (!Objects.equals(source.getSpecificity(), dest.getSpecificity())) {
            LOGGER.warning("Category " + category + ": could not reset mapping specificity for isoform " + dest.getIsoformAccession() + ", keeping specificity '" + dest.getSpecificity() + "' (ignoring src specificity '" + source.getSpecificity() + "')");
        }

        return mergeable;
    }

    private void mergeAnnotationIsoformSpecificities(AnnotationIsoformSpecificity dest, AnnotationIsoformSpecificity source) {

        if (source.getName() != null && !source.getName().isEmpty()) {
            dest.setName(source.getName());
        }
    }

    @Override
    public List<Annotation> getOriginalAnnotations() {

        return annotations;
    }

    private static class DestAnnotAndOtherSources {

        private final Annotation toUpdateAnnotation;
        private final List<Annotation> sourceAnnotations;

        private DestAnnotAndOtherSources(Annotation toUpdateAnnotation, List<Annotation> sourceAnnotations) {
            this.toUpdateAnnotation = toUpdateAnnotation;
            this.sourceAnnotations = sourceAnnotations;
        }

        private Annotation getDestAnnotation() {
            return toUpdateAnnotation;
        }

        private List<Annotation> getSourceAnnotations() {
            return sourceAnnotations;
        }
    }

}
