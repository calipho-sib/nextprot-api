package org.nextprot.api.user.dao;

import com.google.common.collect.SetMultimap;
import org.nextprot.api.user.domain.UserQuery;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
	 * Get a list of published user queries that not belong to user 'nextprot'
	 * @return a list of user queries
	 */
	List<UserQuery> getPublishedQueries();

	/**
	 * Get the tags associated with user queries
	 * @param queryIds the user queries
	 * @return a map of tags indexed by query id
	 */
	SetMultimap<Long, String> getQueryTags(Collection<Long> queryIds);

	/**
	 * Insert a new user query into database
	 *
 	 * @param userQuery instance used to insert a record into database
	 * @return the generated key
	 */
	long createUserQuery(UserQuery userQuery);

	/**
	 * Add tags for user query named {@code queryId}
	 *
	 * @param queryId user query identifier
	 * @param tags set of tags
	 */
	void createUserQueryTags(long queryId, Set<String> tags);

	/**
	 * Modify user query record identified by the given {@code userQuery} with
	 * data accessible from this same object
	 *
 	 * @param userQuery the user query to update with
	 */
	void updateUserQuery(UserQuery userQuery);

	/**
	 * Delete the user query {@code queryId}
	 *
	 * @param queryId the query id to delete
	 * @return the number of deleted rows
	 */
	int deleteUserQuery(long queryId);

	/**
	 * Delete given tags from query {@code queryId}
	 *
	 * @param queryId the query id to delete tags from
	 * @param tags the set of tags to delete
	 * @return the number of deleted rows
	 */
	int deleteUserQueryTags(long queryId, Set<String> tags);
}
