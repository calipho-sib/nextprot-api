package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.FamilyDao;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Lazy
@Service
class FamilyServiceImpl implements FamilyService {

	@Autowired private FamilyDao familyDao;
	
	@Override
	@Cacheable("families")
	public List<Family> findFamilies(String uniqueName) {
		List<Family> families = familyDao.findFamilies(uniqueName);
		for (Family child: families) {
			while (true) {
				Long childId = child.getFamilyId();
				Family parent = familyDao.findParentOfFamilyId(childId); //TODO can this be done with one query???
				if (parent==null) break;
				child.setParent(parent);
				child = parent; //TODO setting a reference inside a for loop????
			} 
		}

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Family>().addAll(families).build();
	}

}
