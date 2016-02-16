package org.nextprot.api.core.utils.dbxref;

public abstract class StampBaseResolver {

    private final String stamp;

    protected StampBaseResolver(String stamp) {

        this.stamp = "%"+stamp;
    }

    protected String resolve(String templateURL, String accession) {

        return templateURL.replaceFirst(getStamp(), accession);
    }

    protected String getStamp() {

        return stamp;
    }
}
