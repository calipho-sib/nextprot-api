package org.nextprot.api.user.service.impl;

import com.google.common.collect.Sets;
import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
	public UserProteinList createUserProteinList(String listName, String description, Set<String> accessions, String username) {


			UserProteinList proteinList = new UserProteinList();
			proteinList.setName(listName);
			proteinList.setDescription(description);
			proteinList.setAccessions(accessions);

			checkIsAuthorized(proteinList);

			UserProteinList newList = createUserProteinList(proteinList);

			System.out.println("selected: " + proteinList.getAccessionNumbers().size() + " created: " + newList.getAccessionNumbers().size() + " not there: "
					+ Sets.difference(proteinList.getAccessionNumbers(), newList.getAccessionNumbers()));

			return newList;


	}

	@Override
	@Transactional
	public UserProteinList createUserProteinList(UserProteinList proteinList) {
		long id = this.proteinListDao.createUserProteinList(proteinList);
		saveAccessions(id, proteinList.getAccessionNumbers());
		proteinList.setId(id);

		UserProteinList newList = this.proteinListDao.getUserProteinListById(id);
		//newList.setAccessions(this.proteinListDao.getAccessionsByListId(id));
	/*
		System.out.println("selected: " + proteinList.getAccessionNumbers().size() + " created: " + newList.getAccessionNumbers().size() + " not there: "
				+ Sets.difference(proteinList.getAccessionNumbers(), newList.getAccessionNumbers()));
*/
		return proteinList;

	}

	private void saveAccessions(long listId, Set<String> accessions) {
		if (accessions != null && accessions.size() > 0) {
			this.proteinListDao.createUserProteinListAccessions(listId, accessions);
		}
	}

	@Override
	public void deleteUserProteinList(long listId) {
		this.proteinListDao.deleteUserProteinList(listId);
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

		UserProteinList l1 = getUserProteinListByNameForUser(username, list1);
		UserProteinList l2 = getUserProteinListByNameForUser(username, list2);

		Set<String> combined = new HashSet<String>();

		if (op.equals(Operations.AND)) {
			combined.addAll(Sets.intersection(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		} else if (op.equals(Operations.OR)) {
			combined = Sets.union(l1.getAccessionNumbers(), l2.getAccessionNumbers()).immutableCopy();
		} else if (op.equals(Operations.NOT_IN)) {
			combined.addAll(Sets.difference(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		}

		return createUserProteinList(name, description, combined, username);
	}


	private static String checkIsAuthorized(UserProteinList pl){

		String securityUserName;

		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc == null){
			throw new NotAuthorizedException("You must be logged in to access this resource");
		}

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null){
			throw new NotAuthorizedException("You must be logged in to access this resource");
		}

		if (a.getPrincipal() instanceof UserDetails) {
			UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
			securityUserName = currentUserDetails.getUsername();
		} else {
			securityUserName = a.getPrincipal().toString();
		}

		if (!pl.getOwner().equals(securityUserName)) {
			throw new NotAuthorizedException(securityUserName + " is not authorized to modify this resource");
		}

		return securityUserName;


	}


}
