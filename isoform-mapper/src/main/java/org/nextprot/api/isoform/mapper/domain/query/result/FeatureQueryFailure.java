package org.nextprot.api.isoform.mapper.domain.query.result;

import org.nextprot.api.commons.utils.ExceptionWithReason;
import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryResult;

public interface FeatureQueryFailure extends FeatureQueryResult {
	
	ExceptionWithReason.Reason getError();

	default boolean isSuccess() {
		return false;
	}

}
