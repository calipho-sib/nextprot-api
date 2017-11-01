package org.nextprot.api.core.domain.publication;

import org.jsondoc.core.annotation.ApiObject;

import java.io.Serializable;

@ApiObject(name = "global publication statistics", description = "Global publication statistics")
public class GlobalPublicationStatistics implements Serializable {

	private static final long serialVersionUID = 2L;

	private int numberOfCitedPublications;
	private int numberOfComputationallyMappedPublications;
	private int numberOfLargeScalePublications;
	private int numberOfCuratedPublications;

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
}
