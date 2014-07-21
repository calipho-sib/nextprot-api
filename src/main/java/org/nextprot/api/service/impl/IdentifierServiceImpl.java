package org.nextprot.api.service.impl;

import java.util.List;

import org.nextprot.api.dao.IdentifierDao;
import org.nextprot.api.domain.Identifier;
import org.nextprot.api.service.IdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class IdentifierServiceImpl implements IdentifierService {

	@Autowired private IdentifierDao identifierDao;
	
	@Override
	@Cacheable("identifiers")
	public List<Identifier> findIdentifiersByMaster(String uniqueName) {
		return this.identifierDao.findIdentifiersByMaster(uniqueName);		
	}
	
}
