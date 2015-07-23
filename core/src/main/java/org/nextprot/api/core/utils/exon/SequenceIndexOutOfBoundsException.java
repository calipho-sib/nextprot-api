package org.nextprot.api.core.utils.exon;

/**
 *
 * Created by fnikitin on 23/07/15.
 */
public class SequenceIndexOutOfBoundsException extends Exception {

    private final String accession;
    private final int index;
    private final int size;

    public SequenceIndexOutOfBoundsException(String accession, int index, int size) {

        super(accession+" SequenceIndexOutOfBoundsException: index ("+index+") must be less than size ("+size+")");

        this.accession = accession;
        this.index = index;
        this.size = size;
    }

    public String getAccession() {
        return accession;
    }

    public int getSize() {
        return size;
    }

    public int getIndex() {
        return index;
    }
}
