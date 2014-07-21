package org.nextprot.api.service.impl;

import java.util.List;

import org.nextprot.api.dao.MasterIdentifierDao;
import org.nextprot.api.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class MasterIdentifierServiceImpl implements MasterIdentifierService {
	@Autowired
	private MasterIdentifierDao masterIdentifierDao;

	@Override
	@Cacheable("master-unique-names-chromossome")
	public List<String> findUniqueNamesOfChromossome(String chromossome) {
		return this.masterIdentifierDao.findUniqueNamesOfChromossome(chromossome);
	}

	@Override
	@Cacheable("master-unique-names")
	public List<String> findUniqueNames() {
		return this.masterIdentifierDao.findUniqueNames();
	}

	@Override
	@Cacheable("master-unique-name")
	public Long findIdByUniqueName(String uniqueName) {
		return this.masterIdentifierDao.findIdByUniqueName(uniqueName);
	}

}
