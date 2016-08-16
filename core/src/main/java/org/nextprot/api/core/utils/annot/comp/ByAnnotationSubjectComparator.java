package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryUtils;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ByAnnotationSubjectComparator implements Comparator<Annotation> {

    private final Entry entry;
    private final Map<String, Annotation> annotationByHash;

    ByAnnotationSubjectComparator(Entry entry) {

        annotationByHash = EntryUtils.getHashAnnotationMap(entry);
        this.entry = entry;
    }

    @Override
    public int compare(Annotation a1, Annotation a2) {

        if (a1.getVariant() == null || a2.getVariant() == null)
            return 0;

        List<Annotation> subjectAnnotations1 = a1.getSubjectComponents().stream().map(annotationByHash::get).collect(Collectors.toList());
        List<Annotation> subjectAnnotations2 = a2.getSubjectComponents().stream().map(annotationByHash::get).collect(Collectors.toList());

        if (subjectAnnotations1.isEmpty())
            throw new NextProtException("cannot find subject for annotation "+ AnnotationUtils.toString(a1));

        if (subjectAnnotations2.isEmpty())
            throw new NextProtException("cannot find subject for annotation "+ AnnotationUtils.toString(a2));

        Annotation annot1 = subjectAnnotations1.get(0);
        Annotation annot2 = subjectAnnotations2.get(0);

        Comparator<Annotation> comp = AnnotationComparators.newComparator(annot1.getAPICategory(), entry);

        return comp.compare(annot1, annot2);
    }
}
