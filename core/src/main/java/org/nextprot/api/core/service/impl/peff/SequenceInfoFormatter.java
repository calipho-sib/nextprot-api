package org.nextprot.api.core.service.impl.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

/**
 * A Cv descriptor is composed of a key and a value
 * Created by fnikitin on 05/05/15.
 */
public abstract class SequenceInfoFormatter {

    private final SequenceDescriptorKey key;

    SequenceInfoFormatter(SequenceDescriptorKey key) {

        Preconditions.checkNotNull(key);
        this.key = key;
    }

    SequenceDescriptorKey getKey() {
        return key;
    }

    /**
     * Format the sequence info value or null if not defined
     * @param entry the nextprot entry
     * @param isoformAccession the isoform accession
     * @return
     */
    protected abstract String formatValue(Entry entry, String isoformAccession);

    public String format(Entry entry, String isoformAccession) {

        boolean hasIsoform = entry.getIsoforms().stream()
                .map(Isoform::getIsoformAccession)
                .anyMatch(acc -> acc.equals(isoformAccession));

        if (!hasIsoform) {
            throw new IllegalStateException(isoformAccession + " is not an isoform of entry "+ entry.getUniqueName());
        }

        String value = formatValue(entry, isoformAccession);

        if (value != null && !value.isEmpty()) {
            return "\\" + key.getName() + "=" + value;
        }

        return "";
    }
}
