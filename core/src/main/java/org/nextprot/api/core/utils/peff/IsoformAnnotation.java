package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Set;

/**
 * Annotation located on isoform formattable in PEFF specified by the HUPO PSI (PubMed:19132688)
 *
 * Created by fnikitin on 05/05/15.
 */
abstract class IsoformAnnotation implements Location<IsoformAnnotation>, PeffFormatter {

    private final IsoformLocation location;

    protected IsoformAnnotation(String isoformId, Annotation annotation, Set<AnnotationApiModel> supportedApiModel) {

        Preconditions.checkNotNull(isoformId);
        Preconditions.checkNotNull(annotation);
        Preconditions.checkNotNull(supportedApiModel);
        Preconditions.checkArgument(supportedApiModel.contains(annotation.getAPICategory()));

        location = new IsoformLocation(isoformId, Value.of(annotation.getStartPositionForIsoform(isoformId)),
                Value.of(annotation.getEndPositionForIsoform(isoformId)));
    }

    public final Value getEnd() {
        return location.getEnd();
    }

    public final Value getStart() {
        return location.getStart();
    }

    @Override
    public int compareTo(IsoformAnnotation other) {

        return location.compareTo(other.location);
    }

    public String toString() {

        return asPeff();
    }
}
