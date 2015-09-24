package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * https://en.wikipedia.org/wiki/Letter_case#Special_case_styles
 *
 * Created by fnikitin on 27/08/15.
 */
public class StringCaseFormatter {

    private final static Pattern DELIMITOR_PATTERN = Pattern.compile("[_\\-\\s]");

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

    private String toCamelCase(final String inputString, boolean firstLetterFirstWordInLowerCase) {

        if (inputString == null)
            return null;

        Matcher matcher = DELIMITOR_PATTERN.matcher(inputString);

        // Do nothing if inputString does not contains delimitors
        if (!matcher.find()) return inputString;

        StringBuilder sb = new StringBuilder(inputString.length());

        for (String word : inputString.split("[-_\\s]")) {

            if (!word.isEmpty()) {

                if (firstLetterFirstWordInLowerCase) {

                    sb.append(word.toLowerCase());
                    firstLetterFirstWordInLowerCase = false;
                } else {

                    sb.append(word.substring(0, 1).toUpperCase());
                    sb.append(word.substring(1).toLowerCase());
                }
            }
        }

        return sb.toString();
    }

    private String camelToKebabCase(String s){
        return camelToLetterCase(s, "-");
    }

    private String camelToSnakeCase(String s){
        return camelToLetterCase(s, "_");
    }

    private String camelToLetterCase(String s, String delimitor) {

        return s.trim().replaceAll("(\\p{Lower})(\\p{Upper})","$1"+delimitor+"$2").toLowerCase();
    }

    /**
     * @return the transformed string
     */
    public String format() {

        return string;
    }
}
