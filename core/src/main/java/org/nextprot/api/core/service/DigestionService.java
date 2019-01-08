package org.nextprot.api.core.service;

import java.util.List;
import java.util.Set;

public interface DigestionService {

	Set<String> digest(String entryAccession, String proteaseName, int minpeplen, int maxpeplen, int missedCleavage);
	Set<String> digestAllWithTrypsin();
	List<String> getProteaseNames();
}
