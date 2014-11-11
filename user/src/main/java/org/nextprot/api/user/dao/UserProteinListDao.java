package org.nextprot.api.user.dao;

import org.nextprot.api.user.domain.UserProteinList;

import java.util.List;
import java.util.Set;

public interface UserProteinListDao {

	/**
	 * Insert a new user protein list into the database
	 *
	 * @param proteinList the user protein list domain object
	 * @return the generated id number
	 */
    long createUserProteinList(UserProteinList proteinList);

	/**
	 * Insert proteins into list named {@code listId}
	 *
	 * @param listId list identifier
	 * @param accessions set of protein accession number
	 */
    void createUserProteinListAccessions(long listId, Set<String> accessions);

	/**
	 * Fetch the list of {@code UserProteinList} that belongs to {@code username}.
	 * <p>Note that the returned UserProteinList instances does not contain any
	 * accession numbers - they instead hold protein count.</p>
	 *
	 * @param username owner name of the lists
	 * @return a list of UserProteinList
	 */
	List<UserProteinList> getUserProteinLists(String username);

	/**
	 * Get {@code UserProteinList} identified by {@code listId}
	 *
	 * @param listId the list identifier
	 * @return a user protein list instance
	 */
	UserProteinList getUserProteinListById(long listId);

	/**
	 * Get the list of accession numbers found in list {@code listId]
	 * @param listId the list identifier
	 * @return a set of protein accession numbers
	 */
	Set<String> getAccessionsByListId(long listId);

	/**
	 * Get {@code UserProteinList} {@code listId} that belongs to {@code owner}
	 *
	 * @param owner the list owner
	 * @param listName the list name
	 * @return a user protein list instance
	 */
	UserProteinList getUserProteinListByName(String owner, String listName);

	/**
	 * Update user protein list
	 *
	 * @param src the source object that provides content to set row with
	 */
	void updateUserProteinList(UserProteinList src);

	/**
	 * Delete the given proteins
	 *
	 * @param listId the list id to delete protein items from
	 * @param accessions the set of accession numbers to delete
	 * @return the number of deleted rows
	 */
	int deleteProteinListItems(long listId, Set<String> accessions);

	/**
	 * Delete the given list id
	 * @param listId the list id to delete
	 * @return
	 */
	int deleteUserProteinList(long listId);
}
