package org.nextprot.api.core.domain;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioEntry extends BioObject<Entry> {

    public BioEntry() {

        super(BioType.PROTEIN_ENTRY, ResourceType.INTERNAL);

        setDatabase("neXtProt");
    }
}
