package org.nextprot.api.user.dao;

import java.util.List;
import java.util.Set;

import org.nextprot.api.user.domain.UserList;

public interface UserListDao {

	List<UserList> getProteinListsMetadata(String username);
	
	Set<String> getAccessionsByListId(Long listId);
	
	Long saveProteinList(UserList proteinList);
	
	void updateProteinList(UserList proteinList);
	
	void saveProteinListAccessions(Long listId, Set<String> accessions);

	int deleteProteinListAccessions(Long listId, Set<String> accessions);

	int deleteProteinList(long listId);
	
	Set<Long> getAccessionIds(Set<String> accessions);
	
	UserList getProteinListById(long listId);
	
	UserList getProteinListByNameForUserIdentifier(String userIdentifier, String listName);
	
	UserList getProteinListByNameForUser(String username, String listName);
}
