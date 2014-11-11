package org.nextprot.api.user.dao;

import org.nextprot.api.user.domain.UserQuery;

import java.util.List;

public interface UserQueryDao {

	/**
	 * Get list of queries owned by {@code username}
	 *
	 * @param username the query owner
	 * @return user queries
	 */
	List<UserQuery> getUserQueries(String username);

	/**
	 * Get user query identified by {@code id}
	 * @param id the query identifier
	 * @return the user query {@code id}
	 */
	UserQuery getUserQueryById(long id);

	/**
	 * Get list of queries labeled with {@code tag}
	 * @param tag the tag name
	 * @return a list of user queries
	 */
	List<UserQuery> getUserQueriesByTag(String tag);

	/**
	 * Get list of published user queries that not belong to user 'nextprot'
	 * @return a list of user queries
	 */
	List<UserQuery> getPublishedQueries();

	long createUserQuery(UserQuery userQuery);

	void updateUserQuery(UserQuery advancedUserQuery);

	void deleteUserQuery(long id);


}
