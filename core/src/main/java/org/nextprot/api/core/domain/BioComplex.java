package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A group of BioObjects is still a BioObject
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioComplex extends BioObject<List<BioObject<?>>> {

    private final List<BioObject<?>> bioObjects;

    protected BioComplex(BioObject<?>... bioObjects) {

        super(BioType.GROUP, getResourceType(bioObjects));

        this.bioObjects = new ArrayList<>();

        for (BioObject<?> bioObject : bioObjects) {

            this.bioObjects.add(bioObject);
        }
    }

    private static ResourceType getResourceType(BioObject<?>... bioObjects) {

        Set<ResourceType> refs = new HashSet<>();

        for (BioObject<?> bioObject : bioObjects) {

            refs.add(bioObject.getResourceType());
        }

        return (refs.size() == 1) ? refs.iterator().next() : ResourceType.MIXED;
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
