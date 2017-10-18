package org.nextprot.api.commons.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Should be used internally by any Enum class that need to provide a more flexible access to their constant via valueOfKey(key).
 *
 * @param <T> an Enum class
 */
public abstract class EnumDictionary<T extends Enum<T>> {

    private final Class<T> clazz;
    private final Map<String, T> dictionary;

    public EnumDictionary(Class<T> clazz, T[] values) {

        this.clazz = clazz;
        this.dictionary = buildDictionary(values);
    }

    private Map<String, T> buildDictionary(T[] values) {

        Map<String, T> m = new HashMap<>(values.length);

        for (T constant : values) {
            m.put(constant.name(), constant);
        }
        updateDictionary(m);

        return m;
    }


    /**
     * @return true if key is associated with a constant
     */
    public boolean haskey(String key) {

        return dictionary.containsKey(key);
    }

    /**
     * Return the Enum constant associated with the given key
     * @param key
     * @return
     */
    public T valueOfKey(String key) {

        T result = dictionary.get(key);

        if (result != null) {

            return result;
        }

        throw new IllegalArgumentException("No enum constant "+clazz.getSimpleName()+"." + key);
    }

    /**
     * @param dictionary update the dictionary for more flexibility
     */
    protected abstract void updateDictionary(Map<String, T> dictionary);
}
