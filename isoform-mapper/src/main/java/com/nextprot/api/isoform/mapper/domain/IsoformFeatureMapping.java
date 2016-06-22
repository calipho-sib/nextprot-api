package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This domain object stores maps entry isoforms to feature informations
 */
public class IsoformFeatureMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private final HashMap<String, IsoformFeature> features;

	public IsoformFeatureMapping() {
		features = new HashMap<>();
	}

	public void addIsoformFeature(String isoformName, IsoformFeature feature) {

		features.put(isoformName, feature);
	}

	/**
	 * Get isoform feature of the specified isoform
	 *
	 * @param isoformName isoform name
	 * @return IsoformFeature or null if isoformName was not found
     */
	public IsoformFeature getIsoformFeature(String isoformName) {

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
