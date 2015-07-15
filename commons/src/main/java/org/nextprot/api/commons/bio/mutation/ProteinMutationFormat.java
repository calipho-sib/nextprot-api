package org.nextprot.api.commons.bio.mutation;

import java.text.ParseException;

/**
 * <code>ProteinMutationFormat</code> provides the interface for formatting and parsing
 * ProteinMutation.
 *
 * Created by fnikitin on 10/07/15.
 */
public interface ProteinMutationFormat {

    enum AACodeType {
        ONE_LETTER, THREE_LETTER
    }

    /**
     * Formats a <code>ProteinMutation</code>.
     *
     * @param mutation the mutation to format
     * @param type the aa letter code type
     *
     * @return a formatter <code>String</>
     */
    String format(ProteinMutation mutation, AACodeType type);

    /**
     * Parses text from the beginning of the given string to produce a ProteinMutation.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return A <code>ProteinMutation</code> parsed from the string.
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    ProteinMutation parse(String source);
}
