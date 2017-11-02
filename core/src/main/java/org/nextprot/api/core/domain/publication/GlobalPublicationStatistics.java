package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ApiObject(name = "global publication statistics", description = "Global publication statistics")
public class GlobalPublicationStatistics implements Serializable {

	private static final long serialVersionUID = 2L;

	private int numberOfCitedPublications;
	private int numberOfComputationallyMappedPublications;
	private int numberOfLargeScalePublications;
	private int numberOfCuratedPublications;
    private Map<Long, PublicationStatistics> publicationStatisticsById = new HashMap<>();

    public PublicationStatistics getPublicationStatistics(long pubId) {
        return publicationStatisticsById.getOrDefault(pubId, new PublicationStatistics());
    }

    public void putPublicationStatisticsById(long pubId, PublicationStatistics stats) {
        this.publicationStatisticsById.put(pubId, stats);
    }

	public int getNumberOfCitedPublications() {
		return numberOfCitedPublications;
	}

	public void incrementNumberOfCitedPublications() {
		numberOfCitedPublications++;
	}

	public int getNumberOfComputationallyMappedPublications() {
		return numberOfComputationallyMappedPublications;
	}

	public void incrementNumberOfComputationallyMappedPublications() {
		numberOfComputationallyMappedPublications++;
	}

	public int getNumberOfLargeScalePublications() {
		return numberOfLargeScalePublications;
	}

	public void incrementNumberOfLargeScalePublications() {
		numberOfLargeScalePublications++;
	}

	public int getNumberOfCuratedPublications() {
		return numberOfCuratedPublications;
	}

	public void incrementNumberOfCuratedPublications() {
		numberOfCuratedPublications++;
	}

    public static class PublicationStatistics implements Serializable {

        private static final long serialVersionUID = 1L;

        private long publicationId;
        private boolean isCited;
        private boolean isLargeScale;
        private boolean isCurated;
        private boolean isComputed;

        public long getPublicationId() {
            return publicationId;
        }

        public void setPublicationId(long publicationId) {
            this.publicationId = publicationId;
        }

        public boolean isCited() {
            return isCited;
        }

        public void setCited(boolean cited) {
            isCited = cited;
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
}
