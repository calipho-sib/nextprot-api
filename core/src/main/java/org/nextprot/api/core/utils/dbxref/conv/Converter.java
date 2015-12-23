package org.nextprot.api.core.utils.dbxref.conv;

import java.util.List;

/**
 * Convert DbXrefProperty to DbXrefs
 *
 * Created by fnikitin on 22/12/15.
 */
public interface Converter<T, E> {

    /**
     * Convert T type object into list of E type objects
     *
     * @param t instance to convert
     * @return a list of E-type instances
     */
    List<E> convert(T t);
}
