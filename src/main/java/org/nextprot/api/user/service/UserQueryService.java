package org.nextprot.api.user.service;

import java.util.List;

import org.nextprot.api.user.domain.UserQuery;

/**
 * Service to retrieve user queries 
 * 
 * @author dteixeira
 *
 */
public interface UserQueryService {

	
	/**
	 * Gets for a particular username
	 * @param username
	 * @return
	 */
	List<UserQuery> getUserQueries(String username);
	
	/**
	 * Get public queries
	 * @return
	 */
	List<UserQuery> getPublicQueries();

	/**
	 * Get nextprot queries
	 * @return
	 */
	List<UserQuery> getNextprotQueries();

	/**
	 * Create operation for the user query
	 * This method needs authorization (the user of the query must be the same as the one authenticated)
	 * @param userQuery
	 */
	UserQuery createUserQuery(UserQuery userQuery);

	/**
	 * Update operation for the user query
	 * This method needs authorization (the user of the query must be the same as the one authenticated)
	 * @param userQuery
	 */
	UserQuery updateUserQuery(UserQuery userQuery);

	/**
	 * Delete operation for the user query
	 * This method needs authorization (the user of the query must be the same as the one authenticated)
	 * @param userQuery
	 */
	void deleteUserQuery(UserQuery userQuery);

	/**
	 * Gets the corresponding user query or null if it does not exist
	 * @param id
	 * @return
	 */
	UserQuery getUserQueryById(long id);
}
