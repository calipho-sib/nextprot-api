package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Merge annotations by updating and returning target annotation with source annotations
 */
public class AnnotationUpdater extends AnnotationBaseMerger {

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

            // add only different evidences
            all.addAll(src.getEvidences().stream().filter(e -> !dest.getEvidences().contains(e)).collect(Collectors.toList()));
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
