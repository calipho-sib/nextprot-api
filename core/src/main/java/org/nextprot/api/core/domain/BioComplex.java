package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of BioObjects is still a BioObject
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioComplex extends BioObject<List<BioObject<?>>> {

    private final List<BioObject<?>> bioObjects;

    protected BioComplex() {
        super(Kind.GROUP);

        bioObjects = new ArrayList<>();
    }

    public void add(BioObject<?> graphic) {
        bioObjects.add(graphic);
    }

    public int size() {
        return bioObjects.size();
    }

    @Override
    public List<BioObject<?>> getContent() {
        return bioObjects;
    }
}
