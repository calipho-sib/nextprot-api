package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.core.dao.FamilyDao;
import org.nextprot.api.core.dao.KeywordDao;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.Keyword;
import org.nextprot.api.core.service.FamilyService;
import org.nextprot.api.core.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class FamilyServiceImpl implements FamilyService {

	@Autowired private FamilyDao familyDao;
	
	@Override
	@Cacheable("families")
	public List<Family> findFamilies(String uniqueName) {
		List<Family> families = familyDao.findFamilies(uniqueName);
		for (Family child: families) {
			while (true) {
				Long childId = child.getFamilyId();
				Family parent = familyDao.findParentOfFamilyId(childId);
				if (parent==null) break;
				child.setParent(parent);
				child = parent;
			} 
		}
		return families;
	}

}
