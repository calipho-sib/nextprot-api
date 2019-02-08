package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PeptideNamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Lazy
@Service
class PeptideNamesServiceImpl implements PeptideNamesService {

	@Autowired private PeptideMappingDao peptideMappingDao;
	@Autowired private MasterIdentifierService masterIdentifierService;
	
	@Override
	@Cacheable(value = "all-peptide-names", sync = true)
	public List<String> findAllPeptideNamesByMasterId(String uniqueName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(uniqueName);
		List<Map<String,Object>> allMapping = this.peptideMappingDao.findPeptideMappingAnnotationsByMasterId(masterId, true, true);
		Set<String> names = new HashSet<String>(); 
		for (Map<String,Object> map: allMapping) 
			names.add((String)map.get(PeptideMappingDao.KEY_PEP_UNIQUE_NAME));
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<String>().addAll(new ArrayList<String>(names)).build();
	}

	
}
