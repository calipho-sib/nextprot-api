package org.nextprot.api.solr.core;

import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.impl.config.Mode;

public interface QueryConfigurations {

	QueryConfiguration getConfig(Mode mode);
	QueryConfiguration getDefaultConfig();
}
