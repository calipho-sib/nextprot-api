package org.nextprot.rdf.dao;

import java.util.List;

import org.nextprot.rdf.domain.OWLAnnotation;
import org.nextprot.rdf.domain.OWLDatabase;
import org.nextprot.rdf.domain.OWLDatasource;
import org.nextprot.rdf.domain.OWLEvidence;
import org.nextprot.rdf.domain.OWLOntology;

public interface SchemaDao {

	List<OWLOntology> findAllOntology();

	List<OWLAnnotation> findAllAnnotation();

	List<OWLEvidence> findAllEvidence();

	List<OWLDatasource> findAllSource();
	
	List<OWLDatabase> findAllDatabase();

	List<OWLDatabase> findAllProvenance();
	
}
