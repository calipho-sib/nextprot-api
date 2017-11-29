package org.nextprot.api.commons.utils;

import java.util.Collection;
import java.util.Map;
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
    public static <T> Stream<T> nullableListToStream(Collection<T> list) {

        return list == null ? Stream.empty() : list.stream();
    }

    /**
     * Return a stream from a nullable map
     * @param map the map to stream
     * @param <K> the key type
     * @param <V> the value type
     * @return a Stream
     */
    public static <K, V> Stream<Map.Entry<K, V>> nullableMapToStream(Map<K, V> map) {

        return map == null ? Stream.empty() : map.entrySet().stream();
    }
}
