package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;

public interface DbXrefService {

	List<DbXref> findDbXrefsByMaster(@ValidEntry String uniqueName);
	
	/**
	 * [FOR ANTIBODIES]
	 * @param uniqueName
	 * @return
	 */
	List<DbXref> findDbXrefByAccession(String accession);

	List<DbXref> findAllDbXrefs();
	
	List<PublicationDbXref> findDbXRefByPublicationId(Long publicationId);
	
	List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds);

	List<DbXref> findDbXRefByResourceId(Long resourceId);

	List<Long> getAllDbXrefsIds();

	List<DbXref> findDbXRefByIds(List<Long> resourceIds);

	/** Convert DbXrefs of type XrefAnnotationMapping into Annotations */
	List<Annotation> findDbXrefsAsAnnotationsByEntry(String entryName);

}