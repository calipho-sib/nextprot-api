package org.nextprot.api.core.service.impl.peff;

public class SimplePEFFInformation extends PEFFInformation {

    private final String value;

    public SimplePEFFInformation(Key key, String value) {

        super(key);
        this.value = value;
    }

    @Override
    protected String formatValue() {
        return value;
    }
}
