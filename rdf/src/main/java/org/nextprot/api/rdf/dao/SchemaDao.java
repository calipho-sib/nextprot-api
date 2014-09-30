package org.nextprot.api.rdf.dao;

import java.util.List;

import org.nextprot.api.rdf.domain.OWLAnnotation;
import org.nextprot.api.rdf.domain.OWLDatabase;
import org.nextprot.api.rdf.domain.OWLDatasource;
import org.nextprot.api.rdf.domain.OWLEvidence;
import org.nextprot.api.rdf.domain.OWLOntology;

public interface SchemaDao {

	List<OWLOntology> findAllOntology();

	List<OWLAnnotation> findAllAnnotation();

	List<OWLEvidence> findAllEvidence();

	List<OWLDatasource> findAllSource();
	
	List<OWLDatabase> findAllDatabase();

	List<OWLDatabase> findAllProvenance();
	
}
