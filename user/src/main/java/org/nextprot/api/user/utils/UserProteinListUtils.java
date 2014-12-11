package org.nextprot.api.user.utils;

import java.util.HashSet;
import java.util.Set;

import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService.Operations;

import com.google.common.collect.Sets;

public class UserProteinListUtils {

	public static UserProteinList combine(UserProteinList l1, UserProteinList l2, Operations op, String name, String description) {

		Set<String> combined = new HashSet<String>();

		if (op.equals(Operations.AND)) {
			combined.addAll(Sets.intersection(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		} else if (op.equals(Operations.OR)) {
			combined = Sets.union(l1.getAccessionNumbers(), l2.getAccessionNumbers()).immutableCopy();
		} else if (op.equals(Operations.NOT_IN)) {
			combined.addAll(Sets.difference(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		}
		
		UserProteinList combinedProteinList = new UserProteinList();
		combinedProteinList.setName(name);
		combinedProteinList.setDescription(description);
		combinedProteinList.setAccessions(combined);

		
		return combinedProteinList;
		
	}

}
