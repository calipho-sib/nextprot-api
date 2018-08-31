package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.annotation.merge.AnnotationMerger;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Merge annotations by updating and returning target annotation with source annotations
 */
public class AnnotationUpdater implements AnnotationMerger {

    private static final Logger LOGGER = Logger.getLogger(AnnotationUpdater.class.getName());

    @Override
    public Annotation merge(Annotation dest, Annotation source) {

        updateDestEvidences(dest, source);
        if (dest.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE) {
            updateDestDescription(dest, source);
        }
        updateDestAnnotationHash(dest, source);
        updateDestIsoformSpecificityName(dest, source);
        updateDestQualityQualifier(dest, source);
        updateDestBioObject(dest, source);

        return dest;
    }

    /** Update dest evidences with sources evidences */
    private void updateDestEvidences(Annotation dest, Annotation source) {

        List<AnnotationEvidence> all = new ArrayList<>(dest.getEvidences());

        // TODO
        all.addAll(source.getEvidences().stream()
                .filter(e -> !dest.getEvidences().contains(e))
                .collect(Collectors.toList()));

        dest.setEvidences(all);
    }

    /** Update dest description with source */
    private void updateDestDescription(Annotation dest, Annotation source) {

        // TODO
    }

    /** Update dest annotation hash */
    private void updateDestAnnotationHash(Annotation dest, Annotation source) {

        String annotationHash = source.getAnnotationHash();

        if (annotationHash == null || annotationHash.isEmpty())
            throw new NextProtException("annotation hash was not computed for source "+source.getUniqueName());

        dest.setAnnotationHash(annotationHash);
    }

    /** Update dest isoform specificity name (variant name) */
    private void updateDestIsoformSpecificityName(Annotation dest, Annotation source) {

        Map<String, AnnotationIsoformSpecificity> destTargetingIsoMap = dest.getTargetingIsoformsMap();

        for (Map.Entry<String, AnnotationIsoformSpecificity> sourceIsoformSpecificityEntry : source.getTargetingIsoformsMap().entrySet()) {

            String isoformName = sourceIsoformSpecificityEntry.getKey();

            if (destTargetingIsoMap.containsKey(isoformName)) {

                if (doAnnotationIsoformSpecificitiesMergeable(destTargetingIsoMap.get(isoformName),
                        sourceIsoformSpecificityEntry.getValue(), dest.getAPICategory())) {

                    mergeAnnotationIsoformSpecificities(destTargetingIsoMap.get(isoformName), sourceIsoformSpecificityEntry.getValue());
                }
            }
            else {
                destTargetingIsoMap.put(isoformName, sourceIsoformSpecificityEntry.getValue());
            }
        }
    }

    /** Reset dest qualityqualifier to gold if there is at least one gold source */
    private void updateDestQualityQualifier(Annotation dest, Annotation source) {

        if (dest.getQualityQualifier() == null || QualityQualifier.valueOf(dest.getQualityQualifier()) != QualityQualifier.GOLD) {

            if (source.getQualityQualifier() != null && source.getQualityQualifier().equals(QualityQualifier.GOLD.name())) {
                dest.setQualityQualifier(QualityQualifier.GOLD.name());
            }
        }
    }

    private void updateDestBioObject(Annotation dest, Annotation source) {

        BioObject destBioObject = dest.getBioObject();

        BioObject srcBioObject = source.getBioObject();

        if (srcBioObject != null && srcBioObject.getProperties() != null) {
            updateDestBioObjectProperties(destBioObject, srcBioObject.getProperties());
        }
    }

    private void updateDestBioObjectProperties(BioObject destBioObject, Map<String, String> srcBioObjectProperties) {

        Map<String, String> destProperties = destBioObject.getProperties();

        for (Map.Entry<String, String> srcKeyValue : srcBioObjectProperties.entrySet()) {

            if (!destProperties.containsKey(srcKeyValue.getKey())) {
                destBioObject.putPropertyNameValue(srcKeyValue.getKey(), srcKeyValue.getValue());
            }
            else if (!srcKeyValue.getValue().equals(destBioObject.getPropertyValue(srcKeyValue.getKey()))) {
                throw new NextProtException("unexpected value "+destBioObject.getPropertyValue(srcKeyValue.getKey())
                        + " for property "+srcKeyValue.getKey() +" (expected: "+srcKeyValue.getValue()+")");
            }
        }
    }

    private boolean doAnnotationIsoformSpecificitiesMergeable(AnnotationIsoformSpecificity dest, AnnotationIsoformSpecificity source, AnnotationCategory category) {

        boolean ret = true;

        if (!Objects.equals(source.getFirstPosition(), dest.getFirstPosition())) {
            LOGGER.severe("Category " + category + ": different first pos dest=" + dest.getIsoformAccession() + ", src=" + source.getIsoformAccession() + ", pos: src=" + source.getFirstPosition() + ", dest=" + dest.getFirstPosition());
            ret = false;
        }
        if (!Objects.equals(source.getLastPosition(), dest.getLastPosition())) {
            LOGGER.severe("Category " + category + ": different last pos dest=" + dest.getIsoformAccession() + ", src=" + source.getIsoformAccession() + ", pos: src=" + source.getLastPosition() + ", dest=" + dest.getLastPosition());
            ret = false;
        }
        if (!Objects.equals(source.getSpecificity(), dest.getSpecificity())) {
            LOGGER.warning("Category " + category + ": different specificity dest=" + dest.getIsoformAccession() + ", src=" + source.getIsoformAccession() + ", spec: src=" + source.getSpecificity() + ", dest=" + dest.getSpecificity());
        }

        return ret;
    }

    private void mergeAnnotationIsoformSpecificities(AnnotationIsoformSpecificity dest, AnnotationIsoformSpecificity source) {

        if (source.getName() != null && !source.getName().isEmpty()) {
            dest.setName(source.getName());
        }
    }
}
