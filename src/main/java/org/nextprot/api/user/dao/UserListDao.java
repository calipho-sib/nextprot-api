package org.nextprot.api.core.dao;

import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.ProteinList;

public interface ProteinListDao {

	List<ProteinList> getProteinListsMetadata(String username);
	
	Set<String> getAccessionsByListId(Long listId);
	
	Long saveProteinList(ProteinList proteinList);
	
	void updateProteinList(ProteinList proteinList);
	
	void saveProteinListAccessions(Long listId, Set<String> accessions);

	int deleteProteinListAccessions(Long listId, Set<String> accessions);

	int deleteProteinList(long listId);
	
	Set<Long> getAccessionIds(Set<String> accessions);
	
	ProteinList getProteinListById(long listId);
	
	ProteinList getProteinListByNameForUserIdentifier(String userIdentifier, String listName);
	
	ProteinList getProteinListByNameForUser(String username, String listName);
}
