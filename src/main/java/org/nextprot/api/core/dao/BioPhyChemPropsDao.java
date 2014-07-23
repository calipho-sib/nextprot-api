package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.commons.utils.Pair;

public interface BioPhyChemPropsDao {

	List<Pair<String, String>> findPropertiesByUniqueName(String uniqueName);
}
