package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
class ExperimentalContextServiceImpl implements ExperimentalContextService {
	
	@Autowired private ExperimentalContextDao ecDao;
	@Autowired private AnnotationService annotationService;
	@Autowired private TerminologyDao terminologyDao;
	
	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		List<ExperimentalContext> ecs = ecDao.findAllExperimentalContexts();
		updateTerminologies(ecs);
		return ecs;
	}

	@Override
	@Cacheable("experimental-contexts-by-entry")
	public List<ExperimentalContext> findExperimentalContextsByEntryName(String entryName) {	
		//TODO: reimplement exp context dao to get the list without annotations directly from entry name
		List<Annotation> annotations = this.annotationService.findAnnotations(entryName);
		Set<Long> ecSet = AnnotationUtils.getExperimentalContextIdsForAnnotations(annotations);
		List<ExperimentalContext> ecs = ecDao.findExperimentalContextsByIds(new ArrayList<>(ecSet));
		updateTerminologies(ecs);
		return ecs;
	}

	private void updateTerminologies(List<ExperimentalContext> ecs) {

		for (ExperimentalContext ec : ecs) {
			updateTerminologies(ec);
		}
	}

	private void updateTerminologies(ExperimentalContext ec) {

		if (ec.getCellLine() != null) ec.setCellLine(terminologyDao.findTerminologyByAccession(ec.getCellLineAC()));
		if (ec.getTissue() != null) ec.setTissue(terminologyDao.findTerminologyByAccession(ec.getTissueAC()));
		if (ec.getOrganelle() != null) ec.setOrganelle(terminologyDao.findTerminologyByAccession(ec.getOrganelleAC()));
		if (ec.getDetectionMethod() != null) ec.setDetectionMethod(terminologyDao.findTerminologyByAccession(ec.getDetectionMethodAC()));
		if (ec.getDisease() != null) ec.setDisease(terminologyDao.findTerminologyByAccession(ec.getDiseaseAC()));
		if (ec.getDevelopmentalStage() != null) ec.setDevelopmentalStage(terminologyDao.findTerminologyByAccession(ec.getDevelopmentalStageAC()));
	}
}

