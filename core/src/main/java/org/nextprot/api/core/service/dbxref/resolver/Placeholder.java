package org.nextprot.api.core.service.dbxref.resolver;

public abstract class Placeholder {

    private final String text;

    protected Placeholder(String placeHolder) {

        this.text = "%"+placeHolder;
    }

    public String replacePlaceholderWithAccession(String templateURL, String accession) {

        return templateURL.replaceFirst(getPlaceholderText(), accession);
    }

    public String getPlaceholderText() {

        return text;
    }
}
