package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.core.dao.FamilyDao;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Lazy
@Service
class FamilyServiceImpl implements FamilyService {

	private static final Map<String, Integer> REGION_SORT_INDEX = new HashMap<>();

	static {
		// "In the N-terminal section" < "In the C-terminal section" < "In the central section"
		REGION_SORT_INDEX.put("In the N-terminal section", 0);
		REGION_SORT_INDEX.put("In the central section", 1);
		REGION_SORT_INDEX.put("In the C-terminal section", 2);
	}

	private static final Comparator<Family> FAMILY_COMPARATOR = new Comparator<Family>() {
		@Override
		public int compare(Family f1, Family f2) {

			return Integer.compare(REGION_SORT_INDEX.get(f1.getRegion()), REGION_SORT_INDEX.get(f2.getRegion()));
		}
	};

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

		Collections.sort(families, FAMILY_COMPARATOR);

		//returns a immutable list when the result is cacheable (this prevents modifying the cache, since the cache returns a reference) copy on read and copy on write is too much time consuming
		return new ImmutableList.Builder<Family>().addAll(families).build();
	}

}
