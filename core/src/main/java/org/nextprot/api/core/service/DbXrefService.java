package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface DbXrefService {

	List<DbXref> findDbXrefsByMaster(@ValidEntry String uniqueName);
	
	/**
	 * [FOR ANTIBODIES]
	 * @param uniqueName
	 * @return
	 */
	List<DbXref> findDbXrefByAccession(String accession);

	List<DbXref> findAllDbXrefs();
	
	List<DbXref> findDbXrefsByEntry(String uniqueName);
	
	List<DbXref> findDbXrefsAsAnnotByEntry(String uniqueName);

	List<DbXref> findDbXRefByPublicationId(Long publicationId);
	
	List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds);

	List<DbXref> findDbXRefByResourceId(Long resourceId);

	List<Long> getAllDbXrefsIds();

	List<DbXref> findDbXRefByIds(List<Long> resourceIds);
	
}