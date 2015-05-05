package org.nextprot.api.core.utils.peff;

/**
 * A location with a start and an end
 *
 * Created by fnikitin on 05/05/15.
 */
public interface Location<T extends Location<T>> extends Comparable<T>{

    int getStart();
    int getEnd();

}
