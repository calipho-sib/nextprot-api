package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.utils.Pair;

public interface BioPhyChemPropsDao {

	List<Pair<String, String>> findPropertiesByUniqueName(String uniqueName);
}
