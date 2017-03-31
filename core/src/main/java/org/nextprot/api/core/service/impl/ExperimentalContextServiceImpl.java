package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.ExperimentalContextDictionaryService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
class ExperimentalContextServiceImpl implements ExperimentalContextService {
	
	@Autowired private ExperimentalContextDictionaryService experimentalContextDictionaryService;
	
	
	@Override
	public List<ExperimentalContext> findExperimentalContextsByAnnotations(List<Annotation> annotations) {
		
		Map<Long,ExperimentalContext> ecMap =  experimentalContextDictionaryService.getAllExperimentalContexts();
		Set<ExperimentalContext> ecs = new TreeSet<>(new IdComparator());
		if (annotations != null) {
			for (Annotation annot : annotations) {
				if (annot.getEvidences() != null) {
					for (AnnotationEvidence evi: annot.getEvidences()) {
						Long ecId = evi.getExperimentalContextId();
						if (ecId != null && ecId != 0) {
							ExperimentalContext ec = ecMap.get(ecId);
							if (ec != null) ecs.add(ec);
						}
					}
				}
			}
		}
		return new ArrayList<ExperimentalContext>(ecs);
	}	
	
	
	@Override
	public List<ExperimentalContext> findAllExperimentalContexts() {
		
		return new ArrayList<ExperimentalContext>(experimentalContextDictionaryService.getAllExperimentalContexts().values());
	}
	

	private class IdComparator implements Comparator<ExperimentalContext> {
		@Override
		public int compare(ExperimentalContext ec1, ExperimentalContext ec2) {
			return Long.compare(ec1.getContextId(), ec2.getContextId());
		}
	}

}

