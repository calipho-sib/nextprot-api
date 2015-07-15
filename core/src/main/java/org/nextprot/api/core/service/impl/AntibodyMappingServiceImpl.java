package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.dao.AntibodyMappingDao;
import org.nextprot.api.core.domain.AntibodyMapping;
import org.nextprot.api.core.service.AntibodyMappingService;
import org.nextprot.api.core.service.DbXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
class AntibodyMappingServiceImpl implements AntibodyMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private AntibodyMappingDao antibodyMappingDao;
	@Autowired private DbXrefService xrefService;

	@Override
	@Cacheable("antibodies")
	public List<AntibodyMapping> findAntibodyMappingByUniqueName(String entryName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(entryName);
		List<AntibodyMapping> mappings = this.antibodyMappingDao.findAntibodiesById(masterId);
		for(AntibodyMapping mapping : mappings) {
			//System.out.println("Antibody mapping before setting xref" + mapping.toString());
			mapping.setXrefs(this.xrefService.findDbXRefByResourceId(mapping.getXrefId()));
		}
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference)
		return new ImmutableList.Builder<AntibodyMapping>().addAll(mappings).build();
	}
	
}
