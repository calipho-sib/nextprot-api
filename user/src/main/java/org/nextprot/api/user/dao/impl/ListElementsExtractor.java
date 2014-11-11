package org.nextprot.api.user.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A generic ResultSetExtractor to extract list of elements E
 *
 * Created by fnikitin on 24/10/14.
 */
abstract class ListElementsExtractor<E extends Object> implements ResultSetExtractor<List<E>> {

    public List<E> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        LinkedHashMap<Long, E> elementsById = new LinkedHashMap<Long, E>();

        Set<String> set;

        while (resultSet.next()) {

            long id = resultSet.getLong(getListIdName());
            String elementName = resultSet.getString(getElementName());

            if (!elementsById.containsKey(id)) {

                E element = createElement();

                set = new HashSet<String>();

                setElement(id, resultSet, element, set);

                elementsById.put(id, element);
            } else {

                set = getSet(elementsById.get(id));
            }

            if (elementName != null) set.add(elementName);
        }

        List<E> users = new ArrayList<E>(elementsById.values());
        return users;
    }

    protected abstract E createElement();
    protected abstract void setElement(long id, ResultSet result, E element, Set<String> set) throws SQLException;
    protected abstract Set<String> getSet(E element);
    protected abstract String getListIdName();
    protected abstract String getElementName();
}