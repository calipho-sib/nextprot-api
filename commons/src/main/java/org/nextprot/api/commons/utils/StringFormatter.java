package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fluently format a string.
 *
 * https://en.wikipedia.org/wiki/Letter_case#Special_case_styles
 *
 * Created by fnikitin on 27/08/15.
 */
public class StringFormatter {

    private final static Pattern DELIMITOR_PATTERN = Pattern.compile("[_\\-\\s]");

    private String stringToFormat;

    public StringFormatter(String stringToFormat) {

        Preconditions.checkNotNull(stringToFormat);

        this.stringToFormat = stringToFormat;
    }

    /**
     * Remove spaces, hyphens and underscores and the first letter of each word is capitalised except the first one
     */
    public StringFormatter camel() {

        return camelFirstWordLetterLowerCase(true);
    }

    /**
     * Remove spaces, hyphens and underscores and the first letter of each word is capitalised (the first letter
     * case is decided from parameter <code>firstLetterFirstWordInLowerCase</code>)
     *
     * @param firstWordLetterInLowerCase true if the 1st letter of the first word has to be in lower case
     */
    public StringFormatter camelFirstWordLetterLowerCase(final boolean firstWordLetterInLowerCase) {

        stringToFormat = toCamelCase(stringToFormat, firstWordLetterInLowerCase);

        return this;
    }

    /**
     * Replaces the Capital letters with lower letters and prefixed with a hyphen if not in the beginning of the stringToFormat.
     */
    public StringFormatter kebab() {

        stringToFormat = camelToKebabCase(stringToFormat);

        return this;
    }

    /**
     * Replaces the Capital letters with lower letters and prefixed with an underscore if not in the beginning of the stringToFormat.
     */
    public StringFormatter snake() {

        stringToFormat = camelToSnakeCase(stringToFormat);

        return this;
    }

    /**
     * Converts all characters to upper case
     */
    public StringFormatter yelling() {

        stringToFormat = stringToFormat.toUpperCase();
        return this;
    }

    /**
     * Converts all characters to lower case
     */
    public StringFormatter whispering() {

        stringToFormat = stringToFormat.toLowerCase();
        return this;
    }

    private String toCamelCase(final String inputString, final boolean firstLetterFirstWordInLowerCase) {

        if (inputString == null)
            return null;

        Matcher matcher = DELIMITOR_PATTERN.matcher(inputString);

        // Do nothing if inputString does not contains delimitors
        if (!matcher.find()) return inputString;

        StringBuilder sb = new StringBuilder(inputString.length());

        boolean toLowerCase = firstLetterFirstWordInLowerCase;

        for (String word : inputString.split("[-_\\s]")) {

            if (!word.isEmpty()) {

                if (toLowerCase) {

                    sb.append(word.toLowerCase());
                    toLowerCase = false;
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

    public String format() {

        return stringToFormat;
    }
}
