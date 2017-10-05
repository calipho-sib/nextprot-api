package org.nextprot.api.core.service.impl.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

/**
 * A Cv descriptor is composed of a key and a value
 * Created by fnikitin on 05/05/15.
 */
public abstract class SequenceInfoFormat {

    private final SequenceDescriptorKey key;

    SequenceInfoFormat(SequenceDescriptorKey key) {

        Preconditions.checkNotNull(key);
        this.key = key;
    }

    /**
     * Format the sequence info value or null if not defined
     */
    protected abstract String formatValue();

    public String format() {

        String value = formatValue();

        if (value != null && !value.isEmpty()) {
            return "\\" + key.getName() + "=" + value;
        }

        return "";
    }
}
