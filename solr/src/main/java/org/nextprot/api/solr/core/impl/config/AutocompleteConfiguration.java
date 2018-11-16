package org.nextprot.api.solr.core.impl.config;


import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.query.Query;

public class AutocompleteConfiguration<F extends SolrField> extends IndexConfiguration<F> {

	private final static char STAR = '*';
	private final static String RANDOM_STRING = "dfjhgdfjh";
	
	public AutocompleteConfiguration(IndexConfiguration<F> parentConfiguration) {
		super(SearchMode.AUTOCOMPLETE, parentConfiguration);
	}
	
	/**
	 * It is different from the simple build query since
	 * it adds a star to the end of the last incomplete token
	 * if that's the case
	 * 
	 * ex. kin*, kinase
	 * 
	 * @param query
	 * @return
	 */
	@Override
	public String formatQuery(Query query) {
		String initialQuery = query.getQueryStringEscapeColon();
		String queryString = super.formatQuery(query);
		
		// finishes with space
		if(initialQuery.charAt(initialQuery.length()-1) == WHITESPACE.charAt(0)) {
			this.otherParameters.put("facet.prefix", RANDOM_STRING);
		} else {
			String[] tokens = query.getQueryStringEscapeColon().split(WHITESPACE);
			String lastToken = tokens[tokens.length-1].toLowerCase();
			char lastCharOfLastToken = lastToken.charAt(lastToken.length()-1);
			String prefix;
			
			if(lastCharOfLastToken != STAR) {
				queryString += STAR;
				prefix = lastToken;
			} else {
				prefix = lastToken.substring(0, lastToken.length()-1);
			}
				
			this.otherParameters.put("facet.prefix", prefix);
			
		}
		return queryString;
	}
}
