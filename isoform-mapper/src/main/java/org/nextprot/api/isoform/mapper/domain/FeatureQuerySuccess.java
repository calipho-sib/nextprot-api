package org.nextprot.api.isoform.mapper.domain;

import java.util.Map;

import org.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccessImpl.IsoformFeatureResult;

public interface FeatureQuerySuccess extends FeatureQueryResult{
	
    Map<String, IsoformFeatureResult> getData();

	default boolean isSuccess() {
		return true;
	}

}
