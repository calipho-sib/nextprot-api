package org.nextprot.api.user.dao;

import org.nextprot.api.user.domain.User;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UserDao {

    long createUser(User user);

    List<User> getUserList();

    User getUserByUsername(String username) throws DataAccessException;

    void updateUser(User user);

    void deleteUser(User user);
}
