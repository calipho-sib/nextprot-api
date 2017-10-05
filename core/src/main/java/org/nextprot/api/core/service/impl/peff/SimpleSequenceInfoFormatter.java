package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

public class SimpleSequenceInfoFormatter extends SequenceInfoFormat {

    private final String value;

    public SimpleSequenceInfoFormatter(SequenceDescriptorKey key, String value) {

        super(key);
        this.value = value;
    }

    @Override
    protected String formatValue() {
        return value;
    }
}
