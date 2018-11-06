package org.nextprot.api.solr.config;

/**
 * See http://wiki.apache.org/solr/ExtendedDisMax
 * See http://wiki.apache.org/solr/CommonQueryParameters
 * 
 * @author pmichel
 *
 */
public enum IndexParameter {
	FL, // fields returned in response
	QF, // fields searched for words with boost factors
	PF, // fields searched for phrases with boost factors
	FN, // ???
	HI  // ???
}
