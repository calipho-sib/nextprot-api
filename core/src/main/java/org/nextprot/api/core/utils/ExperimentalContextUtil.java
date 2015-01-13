package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.ExperimentalContext;

public class ExperimentalContextUtil {
	
	//pam:ec
	public static List<ExperimentalContext> filterExperimentalContextsByIds(List<ExperimentalContext> expContexts, Set<Long> ids){
		List<ExperimentalContext> ecsFiltered = new ArrayList<ExperimentalContext>();
		for(ExperimentalContext ec : expContexts){
			if(ids.contains(ec.getContextId())){
				ecsFiltered.add(ec);
			}
		}
		return ecsFiltered;
	}


}
