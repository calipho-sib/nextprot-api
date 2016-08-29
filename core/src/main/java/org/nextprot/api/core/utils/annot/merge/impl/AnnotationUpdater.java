package org.nextprot.api.core.utils.annot.merge.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Merge annotations by updating and returning target annotation with source annotations
 */
public class AnnotationUpdater extends AnnotationBaseMerger {

    private static final Logger LOGGER = Logger.getLogger(AnnotationUpdater.class.getName());

    @Override
    protected Annotation getDestAnnotation(Annotation annotation1, Annotation annotation2, Annotation... others) {

        return annotation1;
    }

    @Override
    protected List<Annotation> getSourceAnnotations(Annotation annotation1, Annotation annotation2, Annotation... others) {

        List<Annotation> sources = new ArrayList<>();

        sources.add(annotation2);

        for (Annotation other : others) {

            sources.add(other);
        }

        return sources;
    }

    @Override
    protected void updateDestEvidences(Annotation dest, List<Annotation> sources) {

        List<AnnotationEvidence> all = new ArrayList<>(dest.getEvidences());

        for (Annotation src : sources) {

            // According to Daniel, all evidences are different
            all.addAll(src.getEvidences()); //.stream().filter(e -> !dest.getEvidences().contains(e)).collect(Collectors.toList()));
        }

        dest.setEvidences(all);
    }

    @Override
    protected void updateDestAnnotationHash(Annotation dest, List<Annotation> sources) {

        String annotationHash = sources.get(0).getAnnotationHash();

        if (annotationHash == null || annotationHash.isEmpty())
            throw new NextProtException("annotation hash was not computed for source "+sources.get(0).getAnnotationName());

        checkUniqueAnnotationHash(annotationHash, sources);
        dest.setAnnotationHash(annotationHash);
    }

    @Override
    protected void updateDestAnnotationName(Annotation dest, List<Annotation> sources) {

        dest.setAnnotationName(sources.get(0).getAnnotationName());
    }

    @Override
    protected void updateDestIsoformSpecificityName(Annotation dest, List<Annotation> sources) {

        Map<String, AnnotationIsoformSpecificity> destTargetingIsoMap = dest.getTargetingIsoformsMap();

        if (sources.size() > 0) {
            Map<String, AnnotationIsoformSpecificity> srcTargetingIsoMap = sources.get(0).getTargetingIsoformsMap();

            for (Map.Entry<String, AnnotationIsoformSpecificity> keyValue : srcTargetingIsoMap.entrySet()) {

                if (destTargetingIsoMap.containsKey(keyValue.getKey())) {
                    destTargetingIsoMap.get(keyValue.getKey()).setName(keyValue.getValue().getName());
                }
                else {
                    LOGGER.severe("expected isoform specificity not found for isoform "+keyValue.getKey());
                }
            }
        }
        else {
            LOGGER.warning("too many sources to be used to update annotation isoform specificity names");
        }
    }

    @Override
    protected void updateDestBioObject(Annotation dest, List<Annotation> sources) {

        BioObject destBioObject = dest.getBioObject();

        for (Annotation src : sources) {

            BioObject srcBioObject = src.getBioObject();

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
            }
            else if (!srcKeyValue.getValue().equals(destBioObject.getPropertyValue(srcKeyValue.getKey()))) {
                throw new NextProtException("unexpected value "+destBioObject.getPropertyValue(srcKeyValue.getKey())
                        + "for property "+srcKeyValue.getKey() +" (expected: "+srcKeyValue.getValue()+")");
            }
        }
    }

    /**
     * Get annotation hash of given annotations
     * @param sources the source annotations
     * @return a unique annotation hash
     * @throws NextProtException is similar annotation sources do not have the same annotation hash
     */
    private void checkUniqueAnnotationHash(String annotationHash, List<Annotation> sources) {

        for (Annotation source : sources) {

            String hash = source.getAnnotationHash();

            if (hash == null || hash.isEmpty())
                throw new NextProtException("annotation hash was not computed for source "+source.getAnnotationName());
            else if (!annotationHash.equals(hash)) {
                throw new NextProtException("annotation hash differ for similar sources "+sources+" (expected: "+annotationHash+")");
            }
        }
    }
}
