package org.nextprot.api.commons.utils;

import java.util.List;
import java.util.stream.Stream;

/**
 * Utility methods for Streams
 *
 * Created by fnikitin on 12.07.17.
 */
public class StreamUtils {

    private StreamUtils() {

        throw new AssertionError("non instanciable");
    }

    /**
     * Return a stream from a nullable list
     * @param list the list to stream
     * @param <T> element type
     * @return a Stream
     */
    public static <T> Stream<T> nullableListToStream(List<T> list) {

        return list == null ? Stream.empty() : list.stream();
    }
}
