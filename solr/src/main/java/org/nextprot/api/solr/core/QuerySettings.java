package org.nextprot.api.solr.core;

import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryMode;

import java.util.Set;

public interface QuerySettings<F extends SolrField> {

	/** @return query config in given mode */
	QueryConfiguration<F> getConfig(QueryMode mode);

	/** @return default defined mode */
	QueryMode getDefaultMode();

	/** @return the solr fields to return in the result */
	Set<F> getReturnedFields();
}
