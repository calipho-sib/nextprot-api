package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.UserQuery;

public interface RepositoryUserQueryDao {

	List<UserQuery> getUserQueries(String username);

	List<UserQuery> getPublicQueries();

	long saveUserQuery(UserQuery userQuery);

	void updateUserQuery(UserQuery advancedUserQuery);

	void deleteUserQuery(long id);

	List<UserQuery> getNextprotQueries();

	UserQuery getUserQueryById(long id);
	
}
