package org.nextprot.api.service.impl;

import java.util.List;

import org.nextprot.api.dao.AntibodyMappingDao;
import org.nextprot.api.domain.AntibodyMapping;
import org.nextprot.api.service.AntibodyMappingService;
import org.nextprot.api.service.DbXrefService;
import org.nextprot.api.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AntibodyMappingServiceImpl implements AntibodyMappingService {

	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private AntibodyMappingDao antibodyMappingDao;
	@Autowired private DbXrefService xrefService;
	
	@Override
	@Cacheable("antibodies")
	public List<AntibodyMapping> findAntibodyMappingByMasterId(Long id) {
		List<AntibodyMapping> mappings = this.antibodyMappingDao.findAntibodiesById(id);
		for(AntibodyMapping mapping : mappings) {
			//System.out.println("Antibody mapping before setting xref" + mapping.toString());
			mapping.setXrefs(this.xrefService.findDbXRefByResourceId(mapping.getXrefId()));
		}
		return mappings;
	}

	@Override
	public List<AntibodyMapping> findAntibodyMappingByUniqueName(String entryName) {
		Long masterId = this.masterIdentifierService.findIdByUniqueName(entryName);
		return findAntibodyMappingByMasterId(masterId);
	}
	
}
