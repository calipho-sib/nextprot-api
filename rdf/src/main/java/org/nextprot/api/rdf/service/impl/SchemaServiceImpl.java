package org.nextprot.api.rdf.service.impl;

import org.nextprot.api.rdf.dao.SchemaDao;
import org.nextprot.api.rdf.domain.OWLAnnotation;
import org.nextprot.api.rdf.domain.OWLDatabase;
import org.nextprot.api.rdf.domain.OWLDatasource;
import org.nextprot.api.rdf.domain.OWLEvidence;
import org.nextprot.api.rdf.domain.OWLOntology;
import org.nextprot.api.rdf.domain.Version;
import org.nextprot.api.rdf.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Lazy
@Service
public class SchemaServiceImpl implements SchemaService {
	@Autowired private Properties pom;
	
	@Autowired private SchemaDao schemaDao;

	@Override
	public List<OWLOntology> findAllOntology() {
		return schemaDao.findAllOntology();
	}

	@Override
	public List<OWLAnnotation> findAllAnnotation() {
		return schemaDao.findAllAnnotation();		
	}

	@Override
	@Cacheable(value = "schema", sync = true)
	public List<OWLEvidence> findAllEvidence() {
		return schemaDao.findAllEvidence();
	}

	@Override
	public List<OWLDatasource> findAllSource() {
		return schemaDao.findAllSource();
	}
	@Override
	public List<OWLDatabase> findAllDatabase() {
		return schemaDao.findAllDatabase();
	}

	@Override
	public Version getTemplateVersion() {
		Version v=new Version();
		if(pom.containsKey("describe"))
			v.setName(pom.getProperty("describe"));
		if(pom.containsKey("project.version"))
			v.setVersion(pom.getProperty("project.version"));
		return v;
	}

	
	@Override
	public List<OWLDatabase> findAllProvenance() {
		return schemaDao.findAllProvenance();
	}
}
