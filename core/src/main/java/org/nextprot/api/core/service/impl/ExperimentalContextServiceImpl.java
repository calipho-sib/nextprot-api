package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;


@Lazy
@Service
class ExperimentalContextServiceImpl implements ExperimentalContextService {
	
	@Autowired private ExperimentalContextDao ecDao;
	@Autowired private AnnotationService annotationService;
	@Autowired private TerminologyService terminologyService;
	
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
		
		if(!ecs.isEmpty()){
			updateTerminologies(ecs);
		}

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<ExperimentalContext>().addAll(ecs).build();
	}

	private void updateTerminologies(List<ExperimentalContext> ecs) {

		Set<String> terminologyAccessions = new HashSet<String>();
		  
		for (ExperimentalContext ec : ecs) {
			terminologyAccessions.add(ec.getCellLineAC());
			terminologyAccessions.add(ec.getTissueAC());
			terminologyAccessions.add(ec.getOrganelleAC());
			terminologyAccessions.add(ec.getDetectionMethodAC());
			terminologyAccessions.add(ec.getDiseaseAC());
			terminologyAccessions.add(ec.getDevelopmentalStageAC());
		}
		
		List<CvTerm> terms = terminologyService.findCvTermsByAccessions(terminologyAccessions);
		Map<String, CvTerm> map = new HashMap<>();
		for(CvTerm term : terms){
			map.put(term.getAccession(), term);
		}

		for (ExperimentalContext ec : ecs) {
			updateTerminologies(ec, map);
		}

	}

	private void updateTerminologies(ExperimentalContext ec, Map<String, CvTerm> map) {

		if (ec.getCellLine() != null) ec.setCellLine(map.get(ec.getCellLineAC()));
		if (ec.getTissue() != null) ec.setTissue(map.get(ec.getTissueAC()));
		if (ec.getOrganelle() != null) ec.setOrganelle(map.get(ec.getOrganelleAC()));
		if (ec.getDetectionMethod() != null) ec.setDetectionMethod(map.get(ec.getDetectionMethodAC()));
		if (ec.getDisease() != null) ec.setDisease(map.get(ec.getDiseaseAC()));
		if (ec.getDevelopmentalStage() != null) ec.setDevelopmentalStage(map.get(ec.getDevelopmentalStageAC()));
	}
}

