package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;

/**
 * An isoform Location
 *
 * Created by fnikitin on 05/05/15.
 */
public class IsoformLocation implements Location<IsoformLocation> {

    private final String isoformId;
    private final int start;
    private final int end;

    public IsoformLocation(String isoformId, int start, int end) {

        Preconditions.checkNotNull(isoformId);
        Preconditions.checkArgument(!isoformId.isEmpty());
        Preconditions.checkArgument(start >= 0);
        Preconditions.checkArgument(end>=start);

        this.isoformId = isoformId;
        this.start = start;
        this.end = end;
    }

    public final String getIsoformId() {

        return isoformId;
    }

    @Override
    public final int getEnd() {
        return end;
    }

    @Override
    public final int getStart() {
        return start;
    }

    @Override
    public int compareTo(IsoformLocation other) {

        return Integer.compare(start, other.getStart());
    }
}
