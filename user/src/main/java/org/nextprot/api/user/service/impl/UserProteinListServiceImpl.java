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
	public UserProteinList createUserProteinList(UserProteinList proteinList) {

		NPreconditions.checkNotNull(proteinList, "The protein list should not be null");
		NPreconditions.checkTrue(!proteinList.isPersisted(), "The user protein list should not be already entered");

		long id = proteinListDao.createUserProteinList(proteinList);
		// TODO: not needed ?
		//proteinList.setId(id);
		saveAccessions(id, proteinList.getAccessionNumbers());

		return proteinListDao.getUserProteinListById(id);
	}

	private void saveAccessions(long listId, Set<String> accessions) {

		if (accessions != null && !accessions.isEmpty()) {
			proteinListDao.createUserProteinListAccessions(listId, accessions);
		} else {

			// TODO: empty list allowed ?
			// TODO: throw an exception ?
		}
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

		// TODO: why returning proteinList ?
		return proteinList;
	}

	@Override
	public UserProteinList combine(String name, String description, String username, String list1, String list2, Operations op) {

		UserProteinList l1 = proteinListDao.getUserProteinListByName(username, list1);
		UserProteinList l2 = proteinListDao.getUserProteinListByName(username, list2);
		
		return UserProteinListUtils.combine(l1, l2, op, name, description);
	}
}
