package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.PublicationDbXref;

public interface DbXrefDao {

	List<DbXref> findDbXRefsByPublicationId(Long publicationId);
	
	List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds);
	
	List<DbXref> findDbXrefsByMaster(String uniqueName);
	
	List<DbXrefProperty> findDbXrefsProperties(List<Long> resourceIds);

	List<DbXref> findDbXrefByAccession(String accession);

	List<DbXref> findDbXrefByResourceId(Long resourceId);

	List<DbXref> findAllDbXrefs();

	List<Long> getAllDbXrefsIds();

	List<DbXref> findDbXRefByIds(List<Long> resourceIds);
	
	
}
