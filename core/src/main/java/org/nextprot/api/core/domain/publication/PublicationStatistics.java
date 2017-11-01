package org.nextprot.api.core.domain.publication;

public class PublicationStatistics {

    private static final long serialVersionUID = 1L;

    private long publicationId;
    private boolean isLargeScale;
    private boolean isCurated;
    private boolean isComputed;

    public long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    public boolean isLargeScale() {
        return isLargeScale;
    }

    public void setLargeScale(boolean largeScale) {
        isLargeScale = largeScale;
    }

    public boolean isCurated() {
        return isCurated;
    }

    public void setCurated(boolean curated) {
        isCurated = curated;
    }

    public boolean isComputed() {
        return isComputed;
    }

    public void setComputed(boolean computed) {
        isComputed = computed;
    }
}
