package org.nextprot.api.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nextprot.api.dao.ProteinListDao;
import org.nextprot.api.domain.ProteinList;
import org.nextprot.api.service.ProteinListService;
import org.nextprot.auth.core.domain.NextprotUser;
import org.nextprot.auth.core.service.NextprotUserService;
import org.nextprot.search.config.FieldConfigSet;
import org.nextprot.search.config.IndexConfiguration;
import org.nextprot.search.domain.IndexField;
import org.nextprot.search.domain.IndexParameter;
import org.nextprot.search.domain.Query;
import org.nextprot.search.exception.SearchQueryException;
import org.nextprot.search.service.QueryService;
import org.nextprot.search.solr.SearchResult;
import org.nextprot.search.solr.SolrConfiguration;
import org.nextprot.search.solr.SolrIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

@Lazy
@Service
public class ProteinListServiceImpl implements ProteinListService {

	@Autowired
	private ProteinListDao proteinListDao;
	@Autowired
	private QueryService queryService;
	@Autowired
	private NextprotUserService userService;
	@Autowired
	private SolrConfiguration configuration;

	@Override
	public List<ProteinList> getProteinLists(String username) {
		List<ProteinList> proteinLists = this.proteinListDao.getProteinListsMetadata(username);

		for (ProteinList list : proteinLists) {
			Set<String> accessions = this.proteinListDao.getAccessionsByListId(list.getId());
			list.setAccessions(accessions);
		}
		return proteinLists;
	}

	@Override
	public List<ProteinList> getProteinListsMeta(String username) {
		List<ProteinList> proteinLists = this.proteinListDao.getProteinListsMetadata(username);
		return proteinLists;
	}

	@Override
	public ProteinList createProteinList(String listName, String description, Set<String> accessions, String username) {
		NextprotUser u = this.userService.getUserByUsername(username);

		if (u != null) {
			ProteinList proteinList = new ProteinList();
			proteinList.setName(listName);
			proteinList.setDescription(description);
			proteinList.setAccessions(accessions);
			proteinList.setOwnerId(u.getUserId());
			ProteinList newList = createProteinList(proteinList);

			System.out.println("selected: " + proteinList.getAccessions().size() + " created: " + newList.getAccessions().size() + " not there: "
					+ Sets.difference(proteinList.getAccessions(), newList.getAccessions()));

			return newList;
		}
		return null;
	}

	@Override
	@Transactional
	public ProteinList createProteinList(ProteinList proteinList) {
		Long id = this.proteinListDao.saveProteinList(proteinList);
		saveAccessions(id, proteinList.getAccessions());
		proteinList.setId(id);

		ProteinList newList = this.proteinListDao.getProteinListById(id);
		newList.setAccessions(this.proteinListDao.getAccessionsByListId(id));
		System.out.println("selected: " + proteinList.getAccessions().size() + " created: " + newList.getAccessions().size() + " not there: "
				+ Sets.difference(proteinList.getAccessions(), newList.getAccessions()));

		return proteinList;

	}

	private void saveAccessions(long listId, Set<String> accessions) {
		if (accessions != null && accessions.size() > 0) {
			this.proteinListDao.saveProteinListAccessions(listId, accessions);
		}
	}

	@Override
	public void deleteProteinList(long listId) {
		this.proteinListDao.deleteProteinList(listId);
	}

	@Override
	public ProteinList getProteinListById(long listId) {
		ProteinList result = this.proteinListDao.getProteinListById(listId);

		if (result != null) {
			ProteinList l = result;
			l.setAccessions(this.proteinListDao.getAccessionsByListId(listId));
			return l;
		}
		return null;
	}

	@Override
	public ProteinList getProteinListByNameByUUID(String userIdentifier, String listName) {
		ProteinList result = this.proteinListDao.getProteinListByNameForUserIdentifier(userIdentifier, listName);

		if (result != null) {
			ProteinList proteinList = result;
			proteinList.setAccessions(this.proteinListDao.getAccessionsByListId(proteinList.getId()));
			return proteinList;
		}
		return null;
	}

	@Override
	public ProteinList getProteinListByNameForUser(String username, String listName) {
		ProteinList result = this.proteinListDao.getProteinListByNameForUser(username, listName);

		if (result != null) {
			ProteinList proteinList = result;
			proteinList.setAccessions(this.proteinListDao.getAccessionsByListId(proteinList.getId()));
			return proteinList;
		}
		return null;
	}

	@Override
	@Transactional
	public void addAccessions(long listId, Set<String> accessions) {
		saveAccessions(listId, accessions);
	}

	@Override
	public void removeAccessions(long listId, Set<String> accessions) {
		this.proteinListDao.deleteProteinListAccessions(listId, accessions);
	}

	@Override
	public ProteinList updateProteinList(ProteinList proteinList) {
		this.proteinListDao.updateProteinList(proteinList);
		return proteinList;
	}

	@Override
	public SearchResult getProteinListSearchResult(ProteinList proteinList) throws SearchQueryException {

		Set<String> accessions = proteinList.getAccessions();

		String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());

		SolrIndex index = this.configuration.getIndexByName("entry");
		IndexConfiguration indexConfig = index.getConfig("simple");

		FieldConfigSet fieldConfigSet = indexConfig.getConfigSet(IndexParameter.FL);
		Set<IndexField> fields = fieldConfigSet.getConfigs().keySet();
		getClass();

		String[] fieldNames = new String[fields.size()];

		Iterator<IndexField> it = fields.iterator();

		int counter = 0;
		while (it.hasNext()) {
			fieldNames[counter++] = it.next().getName();
		}

		Query query = new Query(index);
		query.addQuery(queryString);
		query.rows(50);
		// Query query = this.queryService.buildQuery(index, "simple", queryString, null, null, null, "0", "50", null, new String[0]);

		return this.queryService.executeByIdQuery(query, fieldNames);
	}

	@Override
	public ProteinList combine(String name, String description, String username, String list1, String list2, Operations op) {

		ProteinList l1 = getProteinListByNameForUser(username, list1);
		ProteinList l2 = getProteinListByNameForUser(username, list2);

		Set<String> combined = new HashSet<String>();

		if (op.equals(Operations.AND)) {
			combined.addAll(Sets.intersection(l1.getAccessions(), l2.getAccessions()));
		} else if (op.equals(Operations.OR)) {
			combined = Sets.union(l1.getAccessions(), l2.getAccessions()).immutableCopy();
		} else if (op.equals(Operations.NOT_IN)) {
			combined.addAll(Sets.difference(l1.getAccessions(), l2.getAccessions()));
		}

		return createProteinList(name, description, combined, username);
	}

	public static enum Operations {
		AND, OR, NOT_IN
	}

}
