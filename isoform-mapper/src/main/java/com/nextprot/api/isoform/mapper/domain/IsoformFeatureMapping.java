package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This domain object stores maps entry isoforms to feature informations
 */
public class IsoformFeatureMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private final HashMap<String, IsoformFeatureMapping.IsoformFeature> features;

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

	public static class IsoformFeature implements Serializable {

		private static final long serialVersionUID = 1L;

		// TODO: define status according to specifications
		// TODO: if not mapped give a precision of the step ...
		// http error code ? ...
		public enum Status {
			MAPPED, UNMAPPED
		}

		private String isoformName;
		private Integer firstPosition;
		private Integer lastPosition;
		private String message;
		private Status status;

		public String getIsoformName() {
			return isoformName;
		}

		public void setIsoformName(String isoformName) {
			this.isoformName = isoformName;
		}

		public Integer getFirstPosition() {
			return firstPosition;
		}

		public void setFirstPosition(Integer firstPosition) {
			this.firstPosition = firstPosition;
		}

		public Integer getLastPosition() {
			return lastPosition;
		}

		public void setLastPosition(Integer lastPosition) {
			this.lastPosition = lastPosition;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}
	}
}
