package org.nextprot.api.user.dao.impl;

import org.nextprot.api.user.domain.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * Created by fnikitin on 24/10/14.
 */
class UsersExtractor implements ResultSetExtractor<List<User>> {

    private final boolean sort;
    private final Comparator<User> userComparator;

    public UsersExtractor() {

        this(false);
    }

    /**
     * @param sort if true compare by user name
     */
    public UsersExtractor(boolean sort) {

        this.sort = sort;

        if (sort) {
            userComparator = new Comparator<User>() {

                @Override
                public int compare(User u1, User u2) {

                    return u1.getUsername().compareTo(u2.getUsername());
                }
            };
        } else {

            userComparator = null;
        }
    }

    public List<User> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        LinkedHashMap<Long, User> usersById = new LinkedHashMap<Long, User>();

        Set<String> roles;

        while (resultSet.next()) {

            long id = resultSet.getLong("user_id");
            String roleName = resultSet.getString("role_name");

            if (!usersById.containsKey(id)) {

                User user = new User();
                roles = new HashSet<String>();

                user.setId(id);
                user.setUsername(resultSet.getString("user_name"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setRoles(roles);

                usersById.put(id, user);
            } else {

                roles = usersById.get(id).getRoles();
            }

            if (roleName != null) roles.add(roleName);
        }

        List<User> users = new ArrayList<User>(usersById.values());

        if (sort) Collections.sort(users, userComparator);

        return users;
    }
}