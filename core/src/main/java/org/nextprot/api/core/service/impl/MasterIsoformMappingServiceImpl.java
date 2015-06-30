package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.MasterIsoformMappingDao;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.PeptideMapping.PeptideEvidence;
import org.nextprot.api.core.domain.PeptideMapping.PeptideProperty;
import org.nextprot.api.core.service.PeptideMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class MasterIsoformMappingServiceImpl implements MasterIsoformMappingService {

	@Autowired private MasterIsoformMappingDao mimDao;
	
	@Override
	@Cacheable("master-isoform-mapping")
	public Map<String,IsoformSpecificity> findMasterIsoformMappingByMasterUniqueName(String uniqueName) {
		Map<String,IsoformSpecificity> map = new HashMap<String,IsoformSpecificity>();
		List<IsoformSpecificity> specs = mimDao.findIsoformMappingByMaster(uniqueName);
		for (IsoformSpecificity tmpSpec: specs) {
			String isoName = tmpSpec.getIsoformName();
			if ( ! map.containsKey(isoName)) map.put(isoName, new IsoformSpecificity(isoName));
			IsoformSpecificity spec = map.get(isoName);
			spec.addPosition(tmpSpec.getPositions().get(0));
		}
		return map;
	}
}
