package org.nextprot.api.isoform.mapper.domain;

public interface FeatureQueryFailure extends FeatureQueryResult {
	
	FeatureQueryException.ErrorReason getError();

	default boolean isSuccess() {
		return false;
	}
	
}
