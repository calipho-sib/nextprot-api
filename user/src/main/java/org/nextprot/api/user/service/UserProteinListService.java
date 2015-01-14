package org.nextprot.api.user.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.user.domain.UserProteinList;

public interface UserProteinListService {

	public enum Operator {
		AND, OR, NOT_IN
	}

	List<UserProteinList> getUserProteinLists(String username);

	/**
	 * Gets protein list with its items
	 *
	 * @param listId
	 * @return
	 */
	UserProteinList getUserProteinListById(long listId);

	/**
	 * Gets meta information from the list
	 * much more peformant than {@link #getUserProteinListById(long)} because the accessions are not ser
	 * 
	 * @param listId
	 * @return
	 */

	UserProteinList getUserProteinListByNameForUser(String username, String listName);

	UserProteinList createUserProteinList(UserProteinList proteinList);

	UserProteinList updateUserProteinList(UserProteinList proteinList);

	void deleteUserProteinList(UserProteinList proteinList);

	UserProteinList combine(String name, String description, String username, String list1, String list2, Operator operator);
}
