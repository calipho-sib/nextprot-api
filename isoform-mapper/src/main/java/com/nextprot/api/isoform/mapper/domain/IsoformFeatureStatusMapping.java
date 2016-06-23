package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This domain object maps an entry isoform to informations about the validation of the reference sequence that change
 */
public class IsoformFeatureStatusMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private final HashMap<String, IsoformFeatureStatus> features;

	public IsoformFeatureStatusMapping() {
		features = new HashMap<>();
	}

	public void addIsoformFeature(String isoformName, IsoformFeatureStatus feature) {

		features.put(isoformName, feature);
	}

	/**
	 * Get isoform feature of the specified isoform
	 *
	 * @param isoformName isoform name
	 * @return IsoformFeature or null if isoformName was not found
     */
	public IsoformFeatureStatus getIsoformFeature(String isoformName) {

		return features.get(isoformName);
	}

	public boolean hasIsoformFeature(String isoformName) {

		return features.containsKey(isoformName);
	}

	/**
	 * @return the number of isoform features
     */
	public int getIsoformFeatureNumber() {

		return features.size();
	}
}
