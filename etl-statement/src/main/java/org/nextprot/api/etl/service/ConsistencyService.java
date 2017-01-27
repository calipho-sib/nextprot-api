package org.nextprot.api.etl.service;

import java.util.List;

public interface ConsistencyService {
	
	List<String> findMissingPublications();
	
	List<String> findMissingCvTerms();
	
}
