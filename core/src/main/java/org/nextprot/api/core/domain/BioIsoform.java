package org.nextprot.api.core.domain;

/**
 * A wrapper over a nextProt protein isoform
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioIsoform extends BioObject<Isoform> {

    private static final long serialVersionUID = 0L;

    public BioIsoform() {

        super(BioType.PROTEIN_ISOFORM, ResourceType.INTERNAL);

        setDatabase(NEXTPROT);
    }
}
