package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.DbXref;
import org.nextprot.api.domain.DbXref.DbXrefProperty;
import org.nextprot.api.domain.PublicationDbXref;

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
