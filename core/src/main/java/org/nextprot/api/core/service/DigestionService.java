package org.nextprot.api.core.service;

import org.expasy.mzjava.proteomics.mol.digest.Protease;

import java.util.Set;

public interface DigestionService {

	Set<String> digest(String entryAccession, Protease protease, int minpeplen, int maxpeplen, int missedCleavage);
	Set<String> digestAllWithTrypsin();
}
