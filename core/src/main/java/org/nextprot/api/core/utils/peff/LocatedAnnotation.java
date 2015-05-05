package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Annotation located on isoform formattable in PEFF specified by the HUPO PSI (PubMed:19132688)
 *
 * Created by fnikitin on 05/05/15.
 */
public abstract class LocatedAnnotation implements Location<LocatedAnnotation>, PeffFormatter {

    private final IsoformLocation location;

    protected LocatedAnnotation(String isoformId, Annotation annotation) {

        Preconditions.checkNotNull(isoformId);
        Preconditions.checkNotNull(annotation);

        location = new IsoformLocation(isoformId, annotation.getStartPositionForIsoform(isoformId),
                annotation.getEndPositionForIsoform(isoformId));
    }

    public final int getEnd() {
        return location.getEnd();
    }

    public final int getStart() {
        return location.getStart();
    }

    @Override
    public int compareTo(LocatedAnnotation other) {

        return location.compareTo(other.location);
    }

    public String toString() {

        return asPeff();
    }
}
