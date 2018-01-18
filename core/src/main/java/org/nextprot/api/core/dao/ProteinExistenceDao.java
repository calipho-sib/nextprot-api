package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.ProteinExistence;

public interface ProteinExistenceDao {

	ProteinExistence findProteinExistenceUniprot(String uniqueName, ProteinExistence.Source source);
}
