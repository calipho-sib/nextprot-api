package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

/**
 * An annotation located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public abstract class Position implements Comparable<Position> {

    private final int start;
    private final int end;

    protected Position(String isoformId, Annotation annotation) {

        Preconditions.checkNotNull(isoformId);
        Preconditions.checkNotNull(annotation);

        AnnotationIsoformSpecificity target = annotation.getTargetingIsoformsMap().get(isoformId);

        this.start = target.getFirstPosition();
        this.end = target.getLastPosition();
    }

    public final int getEnd() {
        return end;
    }

    public final int getStart() {
        return start;
    }

    @Override
    public int compareTo(Position other) {

        return Integer.compare(start, other.getStart());
    }

    /** @return string formatted as specified in PEFF developed by the HUPO PSI (PubMed:19132688) */
    public abstract String asPeff();

    public String toString() {

        return asPeff();
    }
}
