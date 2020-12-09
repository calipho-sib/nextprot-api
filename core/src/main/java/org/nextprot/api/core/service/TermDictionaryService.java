package org.nextprot.api.core.service;

import java.util.Map;

import org.nextprot.api.core.domain.CvTerm;

public interface TermDictionaryService {
	
	Map<String,CvTerm> getTermDictionary(String ontology);

}
