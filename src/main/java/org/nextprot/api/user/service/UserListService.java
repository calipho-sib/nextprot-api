package org.nextprot.api.user.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.user.domain.UserList;

public interface UserListService {
	

	public enum Operations {
		AND, OR, NOT_IN
	}


	List<UserList> getProteinLists(String username);
	List<UserList> getProteinListsMeta(String username);
	
	UserList getProteinListById(long listId);
	
	UserList getProteinListByNameByUUID(String userIdentifier, String listName);
	UserList getProteinListByNameForUser(String username, String listName);
	
	SearchResult getProteinListSearchResult(UserList proteinList) throws SearchQueryException;

	UserList createProteinList(UserList proteinList);
	UserList createProteinList(String listName, String description, Set<String> accessions, String username);

	void deleteProteinList(long listId);
	void addAccessions(long listId, Set<String> accessions);
	void removeAccessions(long listId, Set<String> accessions);
	
	UserList updateProteinList(UserList proteinList);
	
	
	UserList combine(String name, String description, String username, String list1, String list2, Operations operation);
}
