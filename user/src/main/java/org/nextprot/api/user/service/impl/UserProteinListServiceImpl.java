package org.nextprot.api.user.service.impl;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.ResourceNotFoundException;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.commons.utils.StringGenService;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.utils.UserProteinListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Lazy
@Service
public class UserProteinListServiceImpl implements UserProteinListService {

    private static final String PUBLIC_ID_UNIQUE_CONSTRAINT_NAME = "user_protein_lists_pubid_udx";

    @Autowired
    private StringGenService generator;

	@Autowired
	private UserProteinListDao proteinListDao;

	@Override
	public List<UserProteinList> getUserProteinLists(String username) {
		return this.proteinListDao.getUserProteinLists(username);
	}

	@Override
	@Transactional
	public UserProteinList createUserProteinList(UserProteinList userProteinList) {

		NPreconditions.checkNotNull(userProteinList, "The user protein list should not be null");
		NPreconditions.checkTrue(userProteinList.getId() == 0, "The user protein list should be new");

        generatePubidAndCreate(userProteinList);

        Set<String> accessions = userProteinList.getAccessionNumbers();
        if (accessions != null && !accessions.isEmpty())
            proteinListDao.createUserProteinListItems(userProteinList.getId(), accessions);

		return userProteinList;
	}

    /**
     * Generate and set public_id into userProteinList then invoke dao.createUserProteinList.
     *
     * @param userProteinList the resource to create
     *
     * @throws DuplicateKeyException
     */
    private void generatePubidAndCreate(UserProteinList userProteinList) {

        int maxLoop = 10;

        DuplicateKeyException e;

        int count=0;
        do {
            userProteinList.setPublicId(generator.generateString());

            try {
                long id = proteinListDao.createUserProteinList(userProteinList);
                userProteinList.setId(id);
                e = null;

            } catch (DuplicateKeyException dke) {

                if (!dke.getMessage().contains(PUBLIC_ID_UNIQUE_CONSTRAINT_NAME) || count >= maxLoop)
                    throw dke;

                e = dke;
            }
            count++;
        } while(e != null);
    }

	@Override
	public void deleteUserProteinList(UserProteinList proteinList) {
		proteinListDao.deleteUserProteinList(proteinList.getId());
	}

	@Override
	public UserProteinList getUserProteinListById(long listId) {
		try {
			return proteinListDao.getUserProteinListById(listId);
		}catch(DataAccessException e){
			throw new ResourceNotFoundException("The list you are trying to reach is not accessible"); 
		}
	}

	@Override
	public UserProteinList getUserProteinListByNameForUser(String username, String listName) {
		return proteinListDao.getUserProteinListByName(username, listName);
	}

	@Override
	@Transactional
	public UserProteinList updateUserProteinList(UserProteinList proteinList) {

		proteinListDao.updateUserProteinListMetadata(proteinList);

		Set<String> accs = proteinList.getAccessionNumbers();

		if (accs != null && !accs.isEmpty()) {

			proteinListDao.createUserProteinListItems(proteinList.getId(), proteinList.getAccessionNumbers());
		}

		return proteinListDao.getUserProteinListById(proteinList.getId());
	}

	@Override
	public UserProteinList combine(String name, String description, String username, String listName1, String listName2, Operator op) {

		UserProteinList l1 = proteinListDao.getUserProteinListByName(username, listName1);
		UserProteinList l2 = proteinListDao.getUserProteinListByName(username, listName2);

		return UserProteinListUtils.combine(l1, l2, op, username, name, description);
	}

	@Override
	@AllowedAnonymous
	public UserProteinList getUserProteinListByPublicId(String publicId) {
		return proteinListDao.getUserProteinListByPublicId(publicId);
	}
}
