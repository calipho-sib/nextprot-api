package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Merge annotations by updating and returning target annotation with source annotations
 */
public class AnnotationUpdater extends AnnotationBaseMerger {

    private static final Logger LOGGER = Logger.getLogger(AnnotationUpdater.class.getName());

    @Override
    protected Annotation getDestAnnotation(Annotation annotation1, Annotation annotation2) {

        return annotation1;
    }

    @Override
    protected Annotation getSourceAnnotation(Annotation annotation1, Annotation annotation2) {

        return annotation2;
    }

    @Override
    protected void updateDestEvidences(Annotation dest, Annotation source) {

        List<AnnotationEvidence> all = new ArrayList<>(dest.getEvidences());

        // According to Daniel, all evidences are different
        all.addAll(source.getEvidences()); //.stream().filter(e -> !dest.getEvidences().contains(e)).collect(Collectors.toList()));

        dest.setEvidences(all);
    }

    @Override
    protected void updateDestAnnotationHash(Annotation dest, Annotation source) {

        String annotationHash = source.getAnnotationHash();

        if (annotationHash == null || annotationHash.isEmpty())
            throw new NextProtException("annotation hash was not computed for source "+source.getUniqueName());

        dest.setAnnotationHash(annotationHash);
    }

    @Override
    protected void updateDestIsoformSpecificityName(Annotation dest, Annotation source) {

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

    @Override
    protected void updateDestBioObject(Annotation dest, Annotation source) {

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
