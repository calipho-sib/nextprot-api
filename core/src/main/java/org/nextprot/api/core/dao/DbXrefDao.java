package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.PublicationDbXref;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DbXrefDao {

	List<PublicationDbXref> findDbXRefsByPublicationId(Long publicationId);
	
	List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds);
	
	List<DbXref> findDbXrefsByMaster(String uniqueName);

	/** Find DbXrefs that have to be converted in Annotations */
	List<DbXref> findDbXrefsAsAnnotByMaster(String uniqueName);
	
	List<DbXrefProperty> findDbXrefsProperties(String entryName, List<Long> resourceIds);
	List<DbXref.EnsemblInfos> findDbXrefEnsemblInfos(String uniqueName, List<Long> xrefIds);

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
	Set<DbXref> findAntibodyXrefs(List<Long> ids);
	Map<String,String> getGeneRifBackLinks(long pubId);

	/**
     * Find a xref id given an accession number an a database name
     * @param database the database name
     * @param accession the xref accession number
     * @return an optional xref id
     */
    Optional<Long> findXrefId(String database, String accession);

    /**
     * Find the database id given a database name
     * @param databaseName the database name
     * @return an optional db id
     */
    Optional<Integer> findDatabaseId(String databaseName);
}
