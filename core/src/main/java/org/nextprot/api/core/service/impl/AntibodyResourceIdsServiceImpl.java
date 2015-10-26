package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.AntibodyResourceIdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Lazy
@Service
class AntibodyResourceIdsServiceImpl implements AntibodyResourceIdsService {

	@Autowired private AntibodyMappingDao antibodyMappingDao;
	@Autowired private MasterIdentifierService masterIdentifierService;
	
	@Override
	public List<Long> findAllAntibodyIdsByMasterId(String uniqueName) {

		Long masterId = masterIdentifierService.findIdByUniqueName(uniqueName);

		List<Annotation> annotations = antibodyMappingDao.findAntibodyMappingAnnotationsById(masterId);

		Set<Long> names = new HashSet<>();
		for (Annotation annotation : annotations) {
			for (AnnotationEvidence evidence : annotation.getEvidences()) {
				names.add(evidence.getResourceId());
			}
		}

		return new ImmutableList.Builder<Long>().addAll(new ArrayList<>(names)).build();
	}
}
