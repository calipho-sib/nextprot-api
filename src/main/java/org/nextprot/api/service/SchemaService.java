package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.Version;
import org.nextprot.api.domain.rdf.OWLAnnotation;
import org.nextprot.api.domain.rdf.OWLDatabase;
import org.nextprot.api.domain.rdf.OWLDatasource;
import org.nextprot.api.domain.rdf.OWLEvidence;
import org.nextprot.api.domain.rdf.OWLOntology;


public interface SchemaService {
	/**
	 * RDF schema for ontologies
	 * @return
	 */
	public List<OWLOntology> findAllOntology();

	/**
	 * RDF schema for annotations
	 * @return
	 */
	public List<OWLAnnotation> findAllAnnotation();

	public List<OWLEvidence> findAllEvidence();

	public List<OWLDatasource> findAllSource();	
	public List<OWLDatabase> findAllDatabase();	
		
	List<OWLDatabase> findAllProvenance();	

	public Version getTemplateVersion();
}
