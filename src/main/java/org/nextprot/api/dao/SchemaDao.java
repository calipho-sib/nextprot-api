package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.rdf.OWLAnnotation;
import org.nextprot.api.domain.rdf.OWLDatabase;
import org.nextprot.api.domain.rdf.OWLDatasource;
import org.nextprot.api.domain.rdf.OWLEvidence;
import org.nextprot.api.domain.rdf.OWLOntology;

public interface SchemaDao {

	List<OWLOntology> findAllOntology();

	List<OWLAnnotation> findAllAnnotation();

	List<OWLEvidence> findAllEvidence();

	List<OWLDatasource> findAllSource();
	
	List<OWLDatabase> findAllDatabase();

	List<OWLDatabase> findAllProvenance();
	
}
