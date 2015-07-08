package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.dao.MasterIsoformMappingDao;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.TemporaryIsoformSpecificity;
import org.nextprot.api.core.service.IsoformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
//import org.biojavax.bio.seq.io.UniProtCommentParser.Isoform;

@Lazy
@Service
public class MasterIsoformMappingServiceImpl implements MasterIsoformMappingService {

	@Autowired private MasterIsoformMappingDao masterIsoformMappingDao;
	@Autowired private IsoformService isoformService ;
	
	@Override
	@Cacheable("master-isoform-mapping")
	public List<TemporaryIsoformSpecificity> findMasterIsoformMappingByEntryName(String entryName) {

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// build a map between isoform unique name and isoform main name
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		List<Isoform> isoforms = isoformService.findIsoformsByEntryName(entryName);
		Map<String,String> unique2mainName = new HashMap<String,String>();
		for (Isoform iso: isoforms) {
			String mainName = iso.getMainEntityName().getValue();
			unique2mainName.put(iso.getUniqueName(), mainName);
		}

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		// group partial mappings obtained from DAO by isoform and set isoform name
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
		Map<String,TemporaryIsoformSpecificity> map = new HashMap<String,TemporaryIsoformSpecificity>();
		List<TemporaryIsoformSpecificity> specs = masterIsoformMappingDao.findIsoformMappingByMaster(entryName);
		for (TemporaryIsoformSpecificity tmpSpec: specs) {
			String ac = tmpSpec.getIsoformAc();
			if ( ! map.containsKey(ac)) map.put(ac, new TemporaryIsoformSpecificity(ac));
			TemporaryIsoformSpecificity spec = map.get(ac);
			// replace unique name with main name
			spec.setIsoformName(unique2mainName.get(ac));
			spec.addPosition(tmpSpec.getPositions().get(0));
		}
		List<TemporaryIsoformSpecificity> list = new ArrayList<TemporaryIsoformSpecificity>(map.values());
		Collections.sort(list);
		return list;
	}
}
