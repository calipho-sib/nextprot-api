package org.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.utils.ExceptionWithReason;

public interface FeatureQueryFailure extends FeatureQueryResult {
	
	ExceptionWithReason.Reason getError();

	default boolean isSuccess() {
		return false;
	}

}
