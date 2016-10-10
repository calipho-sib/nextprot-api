package org.nextprot.api.core.utils.annot.merge.impl;

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

        checkTargetingIsoformDiscrepancies(dest, source);

        Map<String, AnnotationIsoformSpecificity> destTargetingIsoMap = dest.getTargetingIsoformsMap();

        for (Map.Entry<String, AnnotationIsoformSpecificity> keyValue : source.getTargetingIsoformsMap().entrySet()) {

            if (destTargetingIsoMap.containsKey(keyValue.getKey())) {
                destTargetingIsoMap.get(keyValue.getKey()).setName(keyValue.getValue().getName());
            }
            else {
                LOGGER.severe("expected isoform specificity not found for isoform "+keyValue.getKey());
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
                        + "for property "+srcKeyValue.getKey() +" (expected: "+srcKeyValue.getValue()+")");
            }
        }
    }

    private void checkTargetingIsoformDiscrepancies(Annotation dest, Annotation source) {

        Map<String, AnnotationIsoformSpecificity> destTargetingIsoMap = dest.getTargetingIsoformsMap();
        Map<String, AnnotationIsoformSpecificity> srcTargetingIsoMap = source.getTargetingIsoformsMap();

        if (!destTargetingIsoMap.keySet().equals(srcTargetingIsoMap.keySet())) {
            LOGGER.warning("Category "+dest.getAPICategory()+": different isoform mapping dest="+dest.getUniqueName()+", src="+source.getUniqueName()+", isoforms: src="+srcTargetingIsoMap.keySet()+", dest="+destTargetingIsoMap.keySet());
        }
        else {
            for (Map.Entry<String, AnnotationIsoformSpecificity> entry : destTargetingIsoMap.entrySet()) {

                AnnotationIsoformSpecificity srcValue = srcTargetingIsoMap.get(entry.getKey());
                AnnotationIsoformSpecificity destValue = destTargetingIsoMap.get(entry.getKey());

                if (!Objects.equals(srcValue.getFirstPosition(), destValue.getFirstPosition())) {
                    LOGGER.warning("Category "+dest.getAPICategory()+": different first pos dest="+dest.getUniqueName()+", src="+source.getUniqueName()+", pos: src="+srcValue.getFirstPosition()+", dest="+destValue.getFirstPosition());
                }
                if (!Objects.equals(srcValue.getLastPosition(), destValue.getLastPosition())) {
                    LOGGER.warning("Category "+dest.getAPICategory()+": different last pos dest="+dest.getUniqueName()+", src="+source.getUniqueName()+", pos: src="+srcValue.getLastPosition()+", dest="+destValue.getLastPosition());
                }
                /*if (!Objects.equals(srcValue.getSpecificity(), destValue.getSpecificity())) {
                    LOGGER.info("Category "+dest.getAPICategory()+": different specificity dest="+dest.getUniqueName()+", src="+source.getUniqueName()+", spec: src="+srcValue.getSpecificity()+", dest="+destValue.getSpecificity());
                }*/
            }
        }
    }
}
