package org.nextprot.api.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.domain.ProteinList;
import org.nextprot.api.service.impl.ProteinListServiceImpl.Operations;
import org.nextprot.search.exception.SearchQueryException;
import org.nextprot.search.solr.SearchResult;

public interface ProteinListService {

	List<ProteinList> getProteinLists(String username);
	List<ProteinList> getProteinListsMeta(String username);
	
	ProteinList getProteinListById(long listId);
	
	ProteinList getProteinListByNameByUUID(String userIdentifier, String listName);
	ProteinList getProteinListByNameForUser(String username, String listName);
	
	SearchResult getProteinListSearchResult(ProteinList proteinList) throws SearchQueryException;

	ProteinList createProteinList(ProteinList proteinList);
	ProteinList createProteinList(String listName, String description, Set<String> accessions, String username);

	void deleteProteinList(long listId);
	void addAccessions(long listId, Set<String> accessions);
	void removeAccessions(long listId, Set<String> accessions);
	
	ProteinList updateProteinList(ProteinList proteinList);
	
	
	ProteinList combine(String name, String description, String username, String list1, String list2, Operations operation);
}
