package org.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;

import java.util.Map;

public interface FeatureQuerySuccess extends FeatureQueryResult{
	
    Map<String, IsoformFeatureResult> getData();

	default boolean isSuccess() {
		return true;
	}
}
