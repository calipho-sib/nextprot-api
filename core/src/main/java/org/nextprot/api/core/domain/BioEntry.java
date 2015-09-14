package org.nextprot.api.core.domain;

/**
 * A wrapper over a nextProt protein isoform
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioEntry extends BioObject<Entry> {

    private static final long serialVersionUID = 0L;

    public BioEntry() {

        super(BioType.PROTEIN, ResourceType.INTERNAL);

        setDatabase(NEXTPROT);
    }
}
