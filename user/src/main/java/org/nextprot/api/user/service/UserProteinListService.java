package org.nextprot.api.user.service;

import java.util.List;

import org.nextprot.api.user.domain.UserProteinList;

public interface UserProteinListService {

	public enum Operations {
		AND, OR, NOT_IN
	}

	List<UserProteinList> getUserProteinLists(String username);

	UserProteinList getUserProteinListById(long listId);

	UserProteinList getUserProteinListByNameForUser(String username, String listName);

	UserProteinList createUserProteinList(UserProteinList proteinList);

	UserProteinList updateUserProteinList(UserProteinList proteinList);

	void deleteUserProteinList(UserProteinList proteinList);

	UserProteinList combine(String name, String description, String username, String list1, String list2, Operations operation);
}
