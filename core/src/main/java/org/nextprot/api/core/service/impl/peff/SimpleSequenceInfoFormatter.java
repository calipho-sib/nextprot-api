package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

public class SimpleSequenceInfoFormatter extends SequenceInfoFormatter {

    private final String value;

    public SimpleSequenceInfoFormatter(SequenceDescriptorKey key, String value) {

        super(key);
        this.value = value;
    }

    @Override
    protected String formatValue(Entry entry, String isoformAccession) {
        return value;
    }
}
