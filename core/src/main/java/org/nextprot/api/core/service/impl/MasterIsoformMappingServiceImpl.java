package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.core.dao.MasterIsoformMappingDao;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.biojavax.bio.seq.io.UniProtCommentParser.Isoform;

@Service
class MasterIsoformMappingServiceImpl implements MasterIsoformMappingService {

	@Autowired private MasterIsoformMappingDao masterIsoformMappingDao;
	@Autowired private IsoformService isoformService ;
	
	@Override
	@Cacheable(value = "master-isoform-mapping", sync = true)
	public List<IsoformSpecificity> findMasterIsoformMappingByEntryName(String entryName) {

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
		Map<String,IsoformSpecificity> map = new HashMap<String,IsoformSpecificity>();
		List<IsoformSpecificity> specs = masterIsoformMappingDao.findIsoformMappingByMaster(entryName);
		for (IsoformSpecificity tmpSpec: specs) {
			String ac = tmpSpec.getIsoformAc();
			if ( ! map.containsKey(ac)) map.put(ac, new IsoformSpecificity(null, ac));
			IsoformSpecificity spec = map.get(ac);
			// replace unique name with main name
			spec.setIsoformMainName(unique2mainName.get(ac));
			spec.addPosition(tmpSpec.getPositions().get(0));
		}
		List<IsoformSpecificity> list = new ArrayList<IsoformSpecificity>(map.values());
		Collections.sort(list);
		
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<IsoformSpecificity>().addAll(list).build();
	}
}
