package org.nextprot.api.user.service.impl;

import org.nextprot.api.commons.exception.NPreconditions;
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

	@Override
	public List<UserProteinList> getUserProteinLists(String username) {
		return this.proteinListDao.getUserProteinLists(username);
	}

	@Override
	@Transactional
	public UserProteinList createUserProteinList(UserProteinList userProteinList) {

		NPreconditions.checkNotNull(userProteinList, "The user protein list should not be null");
		NPreconditions.checkTrue(userProteinList.getId() == 0, "The user protein list should be new");

		long id = proteinListDao.createUserProteinList(userProteinList);
		userProteinList.setId(id);

		Set<String> accessions = userProteinList.getAccessionNumbers();
		if (accessions != null && !accessions.isEmpty()) {
			proteinListDao.createUserProteinListItems(id, accessions);
		}

		return userProteinList;
	}

	@Override
	public void deleteUserProteinList(UserProteinList proteinList) {
		proteinListDao.deleteUserProteinList(proteinList.getId());
	}

	@Override
	public UserProteinList getUserProteinListById(long listId) {
		return proteinListDao.getUserProteinListById(listId);
	}

	@Override
	public UserProteinList getUserProteinListByNameForUser(String username, String listName) {
		return proteinListDao.getUserProteinListByName(username, listName);
	}

	@Override
	@Transactional
	public UserProteinList updateUserProteinList(UserProteinList proteinList) {

		proteinListDao.updateUserProteinListMetadata(proteinList);

		Set<String> accs = proteinList.getAccessionNumbers();

		if (accs != null && !accs.isEmpty()) {

			//Easy way of doing it
			proteinListDao.deleteAllProteinListItems(proteinList.getId());
			proteinListDao.createUserProteinListItems(proteinList.getId(), proteinList.getAccessionNumbers());
		}

		return proteinListDao.getUserProteinListById(proteinList.getId());
	}

	@Override
	public UserProteinList combine(String name, String description, String username, String listName1, String listName2, Operator op) {

		UserProteinList l1 = proteinListDao.getUserProteinListByName(username, listName1);
		UserProteinList l2 = proteinListDao.getUserProteinListByName(username, listName2);

		return UserProteinListUtils.combine(l1, l2, op, username, name, description);
	}
}
