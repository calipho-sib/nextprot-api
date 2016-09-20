package org.nextprot.api.isoform.mapper.domain;

public interface FeatureQueryFailure extends FeatureQueryResult{
	
    public FeatureQueryException.ErrorReason getError();

	default boolean isSuccess() {
		return false;
	}
	
}
