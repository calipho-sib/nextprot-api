package org.nextprot.api.commons.bio.variation;

/**
 * A simple deletion with no associated value.
 *
 * Created by fnikitin on 10/07/15.
 */
public class Deletion implements ProteinSequenceChange<Object> {

    private static Deletion INSTANCE = new Deletion();

    private Deletion() { }

    public static Deletion getInstance() {

        return INSTANCE;
    }

    /**
     * No value is associated with a deletion
     * @return null
     */
    @Override
    public String getValue() {
        return null;
    }
}
