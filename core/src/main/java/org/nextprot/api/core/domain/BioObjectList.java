package org.nextprot.api.core.domain;

import java.util.*;

/**
 * Multiple biological objects
 *
 * Created by fnikitin on 26/08/15.
 */
public class BioObjectList extends BioObject {

    private static final long serialVersionUID = 1L;

    private final List<BioObject> bioObjects;

    public BioObjectList(BioType bioType, BioObject bioObject, BioObject... others) {

        super(bioType, deduceResourceType(bioObject, others), null);

        this.bioObjects = new ArrayList<>();

        populateBioObjects(bioObject, others);
    }

    private void populateBioObjects(BioObject bioObject, BioObject... others) {

        this.bioObjects.add(bioObject);
        Collections.addAll(this.bioObjects, others);
    }

    private static ResourceType deduceResourceType(BioObject bioObject, BioObject... others) {

        Set<ResourceType> refs = new HashSet<>();

        refs.add(bioObject.getResourceType());

        for (BioObject bo : others) {

            refs.add(bo.getResourceType());
        }

        return (refs.size() == 1) ? refs.iterator().next() : ResourceType.MIXED;
    }

    public int size() {
        return bioObjects.size();
    }

    public List<BioObject> getContent() {
        return bioObjects;
    }

    @Override
    protected String toBioObjectString() {

        return "\nUnnamed biological group\n";
    }
}
