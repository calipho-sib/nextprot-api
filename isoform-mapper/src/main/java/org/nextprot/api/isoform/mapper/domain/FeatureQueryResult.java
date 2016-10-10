package org.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;

public interface FeatureQueryResult extends Serializable{

	FeatureQuery getQuery();

	abstract boolean isSuccess();

}
