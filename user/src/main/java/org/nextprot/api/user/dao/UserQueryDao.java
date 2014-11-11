package org.nextprot.api.user.dao;

import org.nextprot.api.user.domain.UserQuery;

import java.util.List;

public interface UserQueryDao {

	List<UserQuery> getUserQueries(String username);

	List<UserQuery> getUserQueriesByTag(String tag);

	List<UserQuery> getPublishedQueries();

	long createUserQuery(UserQuery userQuery);

	void updateUserQuery(UserQuery advancedUserQuery);

	void deleteUserQuery(long id);

	UserQuery getUserQueryById(long id);
}
