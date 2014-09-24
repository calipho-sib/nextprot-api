package org.nextprot.api.user.dao;

import java.util.List;

import org.nextprot.api.user.domain.UserQuery;

public interface UserQueryDao {

	List<UserQuery> getUserQueries(String username);

	List<UserQuery> getPublicQueries();

	long saveUserQuery(UserQuery userQuery);

	void updateUserQuery(UserQuery advancedUserQuery);

	void deleteUserQuery(long id);

	List<UserQuery> getNextprotQueries();

	UserQuery getUserQueryById(long id);
	
}
