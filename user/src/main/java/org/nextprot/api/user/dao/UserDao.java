package org.nextprot.api.user.dao;

import java.util.List;

import org.nextprot.api.user.domain.User;

public interface UserDao {

    long createUser(User user);

    List<User> getUserList();

    User getUserByUsername(String username);

    void updateUser(User user);

    void deleteUser(User user);
}
