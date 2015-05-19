package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;

/**
 * An isoform Location
 *
 * Created by fnikitin on 05/05/15.
 */
public class IsoformLocation implements Location<IsoformLocation> {

    private final String isoformId;
    private final Value start;
    private final Value end;

    public IsoformLocation(String isoformId, int start, int end) {

        this(isoformId, Value.of(start), Value.of(end));
    }

    public IsoformLocation(String isoformId, Value start, Value end) {

        Preconditions.checkNotNull(isoformId);
        Preconditions.checkArgument(!isoformId.isEmpty());
        Preconditions.checkNotNull(start);
        Preconditions.checkNotNull(end);

        this.isoformId = isoformId;
        this.start = start;
        this.end = end;
    }

    public final String getIsoformId() {

        return isoformId;
    }

    @Override
    public final Value getEnd() {
        return end;
    }

    @Override
    public final Value getStart() {
        return start;
    }

    @Override
    public int compareTo(IsoformLocation other) {

        int from = (start.isDefined()) ? start.getValue() : end.getValue();
        int to = (other.getStart().isDefined()) ? other.getStart().getValue() : other.getEnd().getValue();

        return Integer.compare(from, to);
    }
}
