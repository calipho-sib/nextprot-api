package org.nextprot.api.user.service.impl;

import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.utils.UserProteinListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Lazy
@Service
public class UserProteinListServiceImpl implements UserProteinListService {

	@Autowired
	private UserProteinListDao proteinListDao;

	/*public List<UserProteinList> getUserProteinListById(int username) {
		List<UserProteinList> proteinLists = this.proteinListDao.getUserProteinLists(username);

		for (UserProteinList list : proteinLists) {
			Set<String> accessions = this.proteinListDao.getAccessionsByListId(list.getKey());
			list.setAccessions(accessions);
		}
		return proteinLists;
	}*/

	@Override
	public List<UserProteinList> getUserProteinLists(String username) {

		return this.proteinListDao.getUserProteinLists(username);
	}

	@Override
	@Transactional
	public UserProteinList createUserProteinList(UserProteinList proteinList) {
		long id = this.proteinListDao.createUserProteinList(proteinList);
		saveAccessions(id, proteinList.getAccessionNumbers());
		proteinList.setId(id);

		UserProteinList newList = this.proteinListDao.getUserProteinListById(id);
		//newList.setAccessions(this.proteinListDao.getAccessionsByListId(id));
		return proteinList;
	}

	private void saveAccessions(long listId, Set<String> accessions) {
		if (accessions != null && accessions.size() > 0) {
			this.proteinListDao.createUserProteinListAccessions(listId, accessions);
		}
	}

	@Override
	public void deleteUserProteinList(UserProteinList proteinList) {
		this.proteinListDao.deleteUserProteinList(proteinList.getId());
	}

	@Override
	public UserProteinList getUserProteinListById(long listId) {

		return this.proteinListDao.getUserProteinListById(listId);
	}

	@Override
	public UserProteinList getUserProteinListByNameForUser(String username, String listName) {

		return proteinListDao.getUserProteinListByName(username, listName);
	}

	@Override
	@Transactional
	public void addAccessionNumbers(long listId, Set<String> accessions) {
		saveAccessions(listId, accessions);
	}

	@Override
	public void removeAccessionNumbers(long listId, Set<String> accessions) {

		proteinListDao.deleteProteinListItems(listId, accessions);
	}

	@Override
	public UserProteinList updateUserProteinList(UserProteinList proteinList) {

		proteinListDao.updateUserProteinList(proteinList);

		return proteinList;
	}


	@Override
	public UserProteinList combine(String name, String description, String username, String list1, String list2, Operations op) {

		UserProteinList l1 = proteinListDao.getUserProteinListByName(username, list1);
		UserProteinList l2 = proteinListDao.getUserProteinListByName(username, list2);
		
		return UserProteinListUtils.combine(l1, l2, op, name, description);
	}


}
