package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Entry;

/**
 * A Cv descriptor is composed of a key and a value
 * Created by fnikitin on 05/05/15.
 */
public abstract class SequenceInfoFormatter {

    private final SequenceDescriptorKey key;

    public SequenceInfoFormatter(SequenceDescriptorKey key) {

        Preconditions.checkNotNull(key);
        this.key = key;
    }

    SequenceDescriptorKey getKey() {
        return key;
    }

    protected abstract String formatValue(Entry entry, String isoformAccession);

    protected abstract boolean hasValue(Entry entry, String isoformAccession);

    public String format(Entry entry, String isoformAccession) {

        if (isoformAccession != null && !isoformAccession.isEmpty()) {
            return "\\" + key.getName() + "=" + formatValue(entry, isoformAccession);
        }
        return "";
    }
}
