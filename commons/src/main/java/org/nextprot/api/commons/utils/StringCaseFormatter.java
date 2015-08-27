package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

/**
 *
 * https://en.wikipedia.org/wiki/Letter_case#Special_case_styles
 *
 * Created by fnikitin on 27/08/15.
 */
public class StringCaseFormatter {

    private String string;

    public StringCaseFormatter(String string) {

        Preconditions.checkNotNull(string);
        this.string = string;
    }

    /**
     * Remove spaces, hyphens and underscores and the first letter of each word is capitalised except the first one
     */
    public StringCaseFormatter camel() {

        return camel(true);
    }

    /**
     * Remove spaces, hyphens and underscores and the first letter of each word is capitalised (the first letter
     * case is decided from parameter <code>firstLetterFirstWordInLowerCase</code>)
     *
     * @param firstLetterFirstWordInLowerCase true if the 1st letter of the first word has to be in lower case
     */
    public StringCaseFormatter camel(boolean firstLetterFirstWordInLowerCase) {

        string = toCamelCase(string, firstLetterFirstWordInLowerCase);

        return this;
    }

    /**
     * Replaces the Capital letters with lower letters and prefixed with a hyphen if not in the beginning of the string.
     */
    public StringCaseFormatter kebab() {

        string = camelToKebabCase(string);

        return this;
    }

    /**
     * Replaces the Capital letters with lower letters and prefixed with an underscore if not in the beginning of the string.
     */
    public StringCaseFormatter snake() {

        string = camelToSnakeCase(string);

        return this;
    }

    /**
     * Converts all characters to upper case
     */
    public StringCaseFormatter yelling() {

        string = string.toUpperCase();
        return this;
    }

    /**
     * Converts all characters to lower case
     */
    public StringCaseFormatter whispering() {

        string = string.toLowerCase();
        return this;
    }

    private String toCamelCase(final String inputString, boolean firstLetterLowerCase) {

        if (inputString == null)
            return null;

        if (inputString.indexOf('_') == -1 && inputString.indexOf('-') == -1)
            return inputString;

        final StringBuilder ret = new StringBuilder(inputString.length());

        for (final String word : inputString.replaceAll("_", " ").split("[ -]")) {
            if (firstLetterLowerCase) {
                ret.append(word.toLowerCase());
                firstLetterLowerCase = false;
                continue;
            }
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
        }

        return ret.toString();
    }

    private String camelToKebabCase(String s){
        return camelToLetterCase(s, "-");
    }

    private String camelToSnakeCase(String s){
        return camelToLetterCase(s, "_");
    }

    private String camelToLetterCase(String s, String delimitor) {

        return s.trim().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2").replaceAll(" ", delimitor).toLowerCase();
    }

    /**
     * @return the transformed string
     */
    public String format() {

        return string;
    }
}
