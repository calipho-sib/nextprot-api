package org.nextprot.api.user.service;

import org.nextprot.api.user.domain.UserProteinList;

import java.util.List;
import java.util.Set;

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
	 * @return
	 */
	UserProteinList getUserProteinListByNameForUser(String username, String listName);

	UserProteinList createUserProteinList(UserProteinList proteinList);

	UserProteinList updateUserProteinList(UserProteinList proteinList);

	void deleteUserProteinList(UserProteinList proteinList);

	Set<String> getUserProteinListAccessionItemsById(long listId);

	UserProteinList combine(String name, String description, String username, String listName1, String listName2, Operator operator);
}
