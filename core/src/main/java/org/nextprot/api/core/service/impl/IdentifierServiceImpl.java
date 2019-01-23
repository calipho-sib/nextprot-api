package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.core.dao.IdentifierDao;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.core.service.IdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
class IdentifierServiceImpl implements IdentifierService {

	@Autowired private IdentifierDao identifierDao;
	
	@Override
	@Cacheable(value = "identifiers", sync = true)
	public List<Identifier> findIdentifiersByMaster(String uniqueName) {
		
		List<Identifier> identifiers = this.identifierDao.findIdentifiersByMaster(uniqueName);
		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Identifier>().addAll(identifiers).build();
	}
	
}
