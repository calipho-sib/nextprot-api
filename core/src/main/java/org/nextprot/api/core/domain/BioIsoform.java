package org.nextprot.api.core.domain;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioIsoform extends BioObject<Isoform> {

    public BioIsoform() {

        super(Kind.ISOFORM);

        setDatabase("neXtProt");
    }
}
