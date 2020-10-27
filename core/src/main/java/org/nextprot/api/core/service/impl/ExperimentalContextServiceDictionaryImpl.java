package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.ExperimentalContextDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.service.ExperimentalContextDictionaryService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


@Lazy
@Service
class ExperimentalContextDictionaryServiceImpl implements ExperimentalContextDictionaryService {
	
	@Autowired private ExperimentalContextDao ecDao;
	@Autowired private TerminologyService terminologyService;
	
	@Override
	@Cacheable(value = "experimental-context-dictionary", sync = true)
	public Map<Long, ExperimentalContext> getAllExperimentalContexts() {
		
		//long t0 = System.currentTimeMillis(); System.out.println("Building experimental context dictionary...");

		List<ExperimentalContext> ecs = ecDao.findAllExperimentalContexts();
		updateTerminologies(ecs);

		Map<Long,ExperimentalContext> dictionary = new TreeMap<>();
		for (ExperimentalContext ec : ecs) dictionary.put(ec.getContextId(), ec);
		ecs=null;
		
		//System.out.println("Building experimental context dictionary DONE in " + (System.currentTimeMillis() - t0) + "ms");
		
		return dictionary;
	}

	@Override
	@Cacheable(value = "experimental-context-dictionary-by-properties", sync = true)
	public Map<String, ExperimentalContext> getExperimentalContextByProperties() {
		List<ExperimentalContext> experimentalContexts = ecDao.findAllExperimentalContexts();
		Map<String, ExperimentalContext> experimentalContextByPropertyMap = new HashMap<>();
		for( ExperimentalContext experimentalContext : experimentalContexts) {
			experimentalContextByPropertyMap.put(ExperimentalContextUtil.computeMd5ForBgee(experimentalContext.getTissueAC(), experimentalContext.getDevelopmentalStageAC(), experimentalContext.getDetectionMethodAC()), experimentalContext);
		}
		return experimentalContextByPropertyMap;
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
		if (terminologyAccessions.size()>0) {
			List<CvTerm> terms = terminologyService.findCvTermsByAccessions(terminologyAccessions);
			Map<String, CvTerm> map = new HashMap<>();
			for(CvTerm term : terms){
				map.put(term.getAccession(), term);
			}
	
			for (ExperimentalContext ec : ecs) {
				updateTerminologies(ec, map);
			}
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

