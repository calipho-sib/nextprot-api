package org.nextprot.api.user.service;

import org.nextprot.api.user.domain.UserProteinList;

import java.util.List;
import java.util.Set;

public interface UserProteinListService {

	public enum Operations {
		AND, OR, NOT_IN
	}

	List<UserProteinList> getUserProteinLists(String username);

	UserProteinList getUserProteinListById(long listId);

	UserProteinList getUserProteinListByNameForUser(String username, String listName);

	UserProteinList createUserProteinList(UserProteinList proteinList);

	void deleteUserProteinList(UserProteinList proteinList);

	void addAccessionNumbers(long listId, Set<String> accessions);

	void removeAccessionNumbers(long listId, Set<String> accessions);

	UserProteinList updateUserProteinList(UserProteinList proteinList);

	UserProteinList combine(String name, String description, String username, String list1, String list2, Operations operation);
}
