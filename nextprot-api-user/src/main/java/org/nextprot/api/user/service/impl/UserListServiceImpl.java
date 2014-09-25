package org.nextprot.api.user.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.user.dao.UserListDao;
import org.nextprot.api.user.domain.UserList;
import org.nextprot.api.user.service.UserListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

@Lazy
@Service
public class UserListServiceImpl implements UserListService {

	@Autowired
	private UserListDao proteinListDao;

	@Override
	public List<UserList> getProteinLists(String username) {
		List<UserList> proteinLists = this.proteinListDao.getProteinListsMetadata(username);

		for (UserList list : proteinLists) {
			Set<String> accessions = this.proteinListDao.getAccessionsByListId(list.getId());
			list.setAccessions(accessions);
		}
		return proteinLists;
	}

	@Override
	public List<UserList> getProteinListsMeta(String username) {
		List<UserList> proteinLists = this.proteinListDao.getProteinListsMetadata(username);
		return proteinLists;
	}

	@Override
	public UserList createProteinList(String listName, String description, Set<String> accessions, String username) {


			UserList proteinList = new UserList();
			proteinList.setName(listName);
			proteinList.setDescription(description);
			proteinList.setAccessions(accessions);

			checkIsAuthorized(proteinList);

			UserList newList = createProteinList(proteinList);

			System.out.println("selected: " + proteinList.getAccessions().size() + " created: " + newList.getAccessions().size() + " not there: "
					+ Sets.difference(proteinList.getAccessions(), newList.getAccessions()));

			return newList;
	}

	@Override
	@Transactional
	public UserList createProteinList(UserList proteinList) {
		Long id = this.proteinListDao.saveProteinList(proteinList);
		saveAccessions(id, proteinList.getAccessions());
		proteinList.setId(id);

		UserList newList = this.proteinListDao.getProteinListById(id);
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
	public UserList getProteinListById(long listId) {
		UserList result = this.proteinListDao.getProteinListById(listId);

		if (result != null) {
			UserList l = result;
			l.setAccessions(this.proteinListDao.getAccessionsByListId(listId));
			return l;
		}
		return null;
	}

	@Override
	public UserList getProteinListByNameByUUID(String userIdentifier, String listName) {
		UserList result = this.proteinListDao.getProteinListByNameForUserIdentifier(userIdentifier, listName);

		if (result != null) {
			UserList proteinList = result;
			proteinList.setAccessions(this.proteinListDao.getAccessionsByListId(proteinList.getId()));
			return proteinList;
		}
		return null;
	}

	@Override
	public UserList getProteinListByNameForUser(String username, String listName) {
		UserList result = this.proteinListDao.getProteinListByNameForUser(username, listName);

		if (result != null) {
			UserList proteinList = result;
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
	public UserList updateProteinList(UserList proteinList) {
		this.proteinListDao.updateProteinList(proteinList);
		return proteinList;
	}


	@Override
	public UserList combine(String name, String description, String username, String list1, String list2, Operations op) {

		UserList l1 = getProteinListByNameForUser(username, list1);
		UserList l2 = getProteinListByNameForUser(username, list2);

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


	private static String checkIsAuthorized(UserList pl){

		String securityUserName = "";

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

		if (!pl.getUsername().equals(securityUserName)) {
			throw new NotAuthorizedException(securityUserName + " is not authorized to modify this resource");
		}

		return securityUserName;


	}


}
