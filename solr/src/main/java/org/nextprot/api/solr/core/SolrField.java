package org.nextprot.api.solr.core;

/**
 * The schema is the place where you tell Solr how it should build indexes from input documents.
 *
 * A field in solr types of index
 */
public interface SolrField {
	
	String getName();
	String getPublicName();
	boolean hasPublicName();
}
