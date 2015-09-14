package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A base class for multiple biological objects
 *
 * Created by fnikitin on 26/08/15.
 */
public abstract class BioList extends BioObject<List<BioObject<?>>> {

    private static final long serialVersionUID = 0L;

    private final List<BioObject<?>> bioObjects;

    protected BioList(BioType bioType, BioObject<?> bioObject, BioObject<?>... others) {

        super(bioType, deduceResourceType(bioObject, others), null);

        this.bioObjects = new ArrayList<>();

        populateBioObjects(bioObject, others);
    }

    private void populateBioObjects(BioObject<?> bioObject, BioObject<?>... others) {

        this.bioObjects.add(bioObject);
        for (BioObject<?> bo : others) {

            this.bioObjects.add(bo);
        }
    }

    private static ResourceType deduceResourceType(BioObject<?> bioObject, BioObject<?>... others) {

        Set<ResourceType> refs = new HashSet<>();

        refs.add(bioObject.getResourceType());

        for (BioObject<?> bo : others) {

            refs.add(bo.getResourceType());
        }

        return (refs.size() == 1) ? refs.iterator().next() : ResourceType.MIXED;
    }

    public int size() {
        return bioObjects.size();
    }

    @Override
    public List<BioObject<?>> getContent() {
        return bioObjects;
    }
}
