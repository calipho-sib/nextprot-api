package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;
import org.nextprot.api.core.service.impl.DbXrefServiceImpl;

import java.util.List;
import java.util.Map;

public interface DbXrefService {

	List<DbXref> findDbXrefsByMaster(@ValidEntry String uniqueName);

	List<DbXref> findDbXrefByAccession(String accession);

	List<DbXref> findAllDbXrefs();
	
	List<PublicationDbXref> findDbXRefByPublicationId(Long publicationId);
	
	List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds);

	List<DbXref> findDbXRefByResourceId(Long resourceId);

	List<Long> getAllDbXrefsIds();

	List<DbXref> findDbXRefByIds(List<Long> resourceIds);

	/** Convert DbXrefs of type XrefAnnotationMapping into Annotations */
	List<Annotation> findDbXrefsAsAnnotationsByEntry(String entryName);

    /**
     * Find a unique xref id given an accession number and a database name
     * @param database the database name
     * @param accession the xref accession number
     * @return a xref id (generated for non existing statement xrefs)
     * @throws DbXrefServiceImpl.MissingCvDatabaseException if database does not exist in table nextprot.cv_databases
     */
    long findXrefId(String database, String accession) throws DbXrefServiceImpl.MissingCvDatabaseException;

	List<DbXref> findDbXrefsByMasterExcludingBed(String entryName);
	
	/**
	 * To be used to override the default link for GeneRIF labels attached to publication-entry pairs
	 * @param pubId a valid resource_id identifying a publication (internal publication id)
	 * @return a Map with an entry accession as the key and an URL as a link to a EuropePMC web page
	 */
	Map<String,String> getGeneRifBackLinks(long pubId);
}