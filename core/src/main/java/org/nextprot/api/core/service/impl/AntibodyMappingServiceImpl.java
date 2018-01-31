package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class AntibodyMappingServiceImpl implements AntibodyMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private AntibodyMappingDao antibodyMappingDao;

	@Override
	public List<Annotation> findAntibodyMappingAnnotationsByUniqueName(String entryName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(entryName);
		return antibodyMappingDao.findAntibodyMappingAnnotationsById(masterId);
	}
}
