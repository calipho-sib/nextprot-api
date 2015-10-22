package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.DbXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class AntibodyMappingServiceImpl implements AntibodyMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private AntibodyMappingDao antibodyMappingDao;
	@Autowired private DbXrefService xrefService;

	@Override
	public List<Annotation> findAntibodyMappingAnnotationsByUniqueName(String entryName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(entryName);
		List<Annotation> annotations = antibodyMappingDao.findAntibodyMappingAnnotationsById(masterId);

		// TODO: Check w/ pam for DbXrefs:
		/*
		for(AntibodyMapping mapping : mappings) {
			//System.out.println("Antibody mapping before setting xref" + mapping.toString());
			mapping.setXrefs(this.xrefService.findDbXRefByResourceId(mapping.getXrefId()));
		}
		for(Annotation annotation : annotations) {
			annotation.setParentXref(this.xrefService.findDbXRefByResourceId(annotation.getParentXref()));
		}*/

		return annotations;
	}
	
}
