package org.nextprot.api.commons.utils;


/**
 * Provide an instance of EnumConstantDictionary to access enum constants
 *
 * @param <T> an Enum class type
 */
public interface EnumDictionarySupplier<T extends Enum<T>> {

    EnumConstantDictionary<T> getEnumConstantDictionary();
}
