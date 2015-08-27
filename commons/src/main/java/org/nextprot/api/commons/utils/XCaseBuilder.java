package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

/**
 *
 * https://en.wikipedia.org/wiki/Letter_case#Special_case_styles
 *
 * Created by fnikitin on 27/08/15.
 */
public class XCaseBuilder {

    private String string;

    public XCaseBuilder(String string) {

        Preconditions.checkNotNull(string);
        this.string = string;
    }

    public XCaseBuilder camel(boolean avoidFirst) {

        string = toCamelCase(string, avoidFirst);

        return this;
    }

    public XCaseBuilder kebab() {

        string = camelToKebabCase(string);

        return this;
    }

    public XCaseBuilder snake() {

        string = camelToSnakeCase(string);

        return this;
    }

    public XCaseBuilder yelling() {

        string = string.toUpperCase();
        return this;
    }

    public XCaseBuilder whispering() {

        string = string.toLowerCase();
        return this;
    }

    /**
     * Replaces the white spaces and the "-" and transform the String in camel
     * case
     *
     * @see <a href="https://en.wikipedia.org/wiki/Letter_case#Special_case_styles">https://en.wikipedia.org/wiki/Letter_case#Special_case_styles</a>
     *
     * @param inputString
     *            The string to be transformed
     * @param avoidFirst
     *            Define true if you want to discard the first word
     * @return The transformed string in camel case
     */
    private static String toCamelCase(final String inputString, boolean avoidFirst) {

        if (inputString == null)
            return null;

        if (inputString.indexOf('_') == -1 && inputString.indexOf('-') == -1)
            return inputString;

        final StringBuilder ret = new StringBuilder(inputString.length());

        for (final String word : inputString.replaceAll("_", " ").split("[ -]")) {
            if (avoidFirst) {
                ret.append(word.toLowerCase());
                avoidFirst = false;
                continue;
            }
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
        }

        return ret.toString();
    }

    /**
     * Replaces the Capital letters with lower letters and prefixed with a hyphen if not in the beginning of the string.
     * For instance PTM info becomes ptm-info and modifiedResidue becomes modified-residue
     *
     * @see <a href="https://en.wikipedia.org/wiki/Letter_case#Special_case_styles">https://en.wikipedia.org/wiki/Letter_case#Special_case_styles</a>
     *
     * @param s raw string
     * @return the string processed
     */
    private static String camelToKebabCase(String s){
        return camelToLetterCase(s, "-");
    }

    /**
     * Replaces the Capital letters with lower letters and prefixed with a underscore if not in the beginning of the string.
     * For instance PTM info becomes ptm_info and modifiedResidue becomes modified_residue
     *
     * @see <a href="https://en.wikipedia.org/wiki/Letter_case#Special_case_styles">https://en.wikipedia.org/wiki/Letter_case#Special_case_styles</a>
     *
     * @param s raw string
     * @return the string processed
     */
    private static String camelToSnakeCase(String s){
        return camelToLetterCase(s, "_");
    }

    private static String camelToLetterCase(String s, String delimitor) {

        return s.trim().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2").replaceAll(" ", delimitor).toLowerCase();
    }

    public String build() {

        return string;
    }
}
