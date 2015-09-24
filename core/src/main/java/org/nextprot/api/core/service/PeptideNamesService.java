package org.nextprot.api.core.service;

import java.util.List;

public interface PeptideNamesService {
	
	List<String> findAllPeptideNamesByMasterId(String uniqueName);

}
