package org.nextprot.api.core.utils.dbxref.resolver;

public abstract class StampBaseResolver {

    private final String stamp;

    protected StampBaseResolver(String stamp) {

        this.stamp = "%"+stamp;
    }

    public String resolve(String templateURL, String accession) {

        return templateURL.replaceFirst(getStamp(), accession);
    }

    public String getStamp() {

        return stamp;
    }
}
