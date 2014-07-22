package org.nextprot.rdf.service;

import java.util.List;

import org.nextprot.rdf.domain.OWLAnnotation;
import org.nextprot.rdf.domain.OWLDatabase;
import org.nextprot.rdf.domain.OWLDatasource;
import org.nextprot.rdf.domain.OWLEvidence;
import org.nextprot.rdf.domain.OWLOntology;
import org.nextprot.rdf.domain.Version;


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
