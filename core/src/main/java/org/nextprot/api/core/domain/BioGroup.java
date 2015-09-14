package org.nextprot.api.core.domain;

/**
 * A group of any BioObjects
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioGroup extends BioList {

    private static final long serialVersionUID = 0L;

    protected BioGroup(BioObject<?> bioObject, BioObject<?>... others) {

        super(BioType.GROUP, bioObject, others);
    }
}
