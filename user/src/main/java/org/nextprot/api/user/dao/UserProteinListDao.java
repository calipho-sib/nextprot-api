package org.nextprot.api.user.dao;

import org.nextprot.api.user.domain.UserProteinList;
import org.springframework.dao.DataAccessException;

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
    void createUserProteinListItems(long listId, Set<String> accessions);

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
	UserProteinList getUserProteinListById(long listId) throws DataAccessException;

    /**
     * Get {@code UserProteinList} identified by {@code publicId}
     *
     * @param publicId the public id identifier
     * @return the user protein list instance
     */
    UserProteinList getUserProteinListByPublicId(String publicId) throws DataAccessException;

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
	UserProteinList getUserProteinListByName(String owner, String listName) throws DataAccessException;

	/**
	 * Update user protein list name and description
	 *
	 * @param src the source object that provides content to set row with
	 */
	void updateUserProteinListMetadata(UserProteinList src);

	/**
	 * Delete the given items for a protein list
	 *
	 * @param listId the list id to delete protein items from
	 * @param accessions the set of accession numbers to delete
	 * @return the number of deleted rows
	 */
	int deleteProteinListItems(long listId, Set<String> accessions);

	/**
	 * Delete the given list {@code listId}
	 *
	 * @param listId the list id to delete
	 * @return the number of deleted rows
	 */
	int deleteUserProteinList(long listId);

	/**
	 * Delete all the items for a protein list
	 *
	 * @param listId the list id to delete protein items from
	 * @return the number of deleted rows
	 */
	int deleteAllProteinListItems(long listId);
}
