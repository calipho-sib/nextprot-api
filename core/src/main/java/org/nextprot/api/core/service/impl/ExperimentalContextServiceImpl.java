package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
public class ExperimentalContextServiceImpl implements ExperimentalContextService {
	
	@Autowired ExperimentalContextDao ecDao;
	@Autowired AnnotationService annotationService;
	
	/*
	@Override
	public List<ExperimentalContext> findExperimentalContextsByIds(List<Long> ids) {
		return ecDao.findExperimentalContextsByIds(ids);
	}
*/
	
	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		return ecDao.findAllExperimentalContexts();
	}

	@Override
	public List<ExperimentalContext> findExperimentalContextsByEntryName(String entryName) {	
		//TODO: reimplement exp context dao to get the list without annotations directly from entry name
		List<Annotation> annotations = this.annotationService.findAnnotations(entryName);
		Set<Long> ecSet = AnnotationUtils.getExperimentalContextIdsForAnnotations(annotations);
		return ecDao.findExperimentalContextsByIds(new ArrayList<Long>(ecSet));
	}
	
}

