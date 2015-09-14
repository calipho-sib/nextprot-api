package org.nextprot.api.core.domain;

/**
 * A complex of biological objects
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioComplex extends BioList {

    private static final long serialVersionUID = 1L;

    protected BioComplex(BioObject<?> bioObject, BioObject<?>... others) {

        super(BioType.COMPLEX, bioObject, others);
    }
}
