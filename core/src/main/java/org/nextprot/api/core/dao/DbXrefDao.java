package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.PublicationDbXref;

import java.util.List;
import java.util.Set;

public interface DbXrefDao {

	List<DbXref> findDbXRefsByPublicationId(Long publicationId);
	
	List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds);
	
	List<DbXref> findDbXrefsByMaster(String uniqueName);

	/** Find DbXrefs that have to be converted in Annotations */
	List<DbXref> findDbXrefsAsAnnotByMaster(String uniqueName);
	
	List<DbXrefProperty> findDbXrefsProperties(List<Long> resourceIds);

	List<DbXref> findDbXrefByAccession(String accession);

	List<DbXref> findDbXrefByResourceId(Long resourceId);

	List<DbXref> findAllDbXrefs();

	List<Long> getAllDbXrefsIds();

	List<DbXref> findDbXRefByIds(List<Long> resourceIds);
	
	Set<DbXref> findEntryAnnotationsEvidenceXrefs(String entryName);
	Set<DbXref> findEntryIdentifierXrefs(String entryName);
	Set<DbXref> findEntryAttachedXrefs(String entryName);
	Set<DbXref> findEntryInteractionXrefs(String entryName);
	Set<DbXref> findPeptideXrefs(List<String> peptideNames);
	Set<DbXref> findEntryInteractionInteractantsXrefs(String entryName);
	
}
