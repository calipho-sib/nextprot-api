package org.nextprot.api.commons.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Should be used internally by any Enum class that needs to access their constants via a String key that does not match
 * exactly the constant name.
 *
 * @param <T> an Enum class type
 */
public abstract class EnumConstantDictionary<T extends Enum<T>> {

    private final Class<T> clazz;
    private final Map<String, T> constantDictionary;

    public EnumConstantDictionary(Class<T> clazz, T[] values) {

        this.clazz = clazz;
        this.constantDictionary = buildDictionaryOfConstants(values);
    }

    private Map<String, T> buildDictionaryOfConstants(T[] values) {

        Map<String, T> m = new HashMap<>(values.length);

        for (T constant : values) {
            m.put(constant.name(), constant);
        }
        updateDictionaryOfConstants(m);

        return m;
    }

    /**
     * @return true if key is associated with a constant
     */
    public boolean haskey(String key) {

        return constantDictionary.containsKey(key);
    }

    /**
     * Return the constant associated with the given key
     * @param key a string key
     * @return a T-type enum constant or throw IllegalArgumentException if not found
     */
    public T valueOfKey(String key) {

        T result = constantDictionary.get(key);

        if (result != null) {

            return result;
        }

        throw new IllegalArgumentException("No enum constant "+clazz.getSimpleName()+"." + key);
    }

    /**
     * @param dictionary update the dictionary of constants for more flexibility
     */
    protected abstract void updateDictionaryOfConstants(Map<String, T> dictionary);
}
