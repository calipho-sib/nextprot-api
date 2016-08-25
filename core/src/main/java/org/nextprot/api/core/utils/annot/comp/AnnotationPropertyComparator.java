package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.core.domain.annotation.AnnotationProperty;

import java.util.Comparator;

/**
 * Compare AnnotationProperty first by name then by value.
 *
 * Created by fnikitin on 10/11/15.
 */
public class AnnotationPropertyComparator implements Comparator<AnnotationProperty> {

    @Override
    public int compare(AnnotationProperty prop1, AnnotationProperty prop2) {

        int cmp = prop1.getName().compareTo(prop2.getName());

        if (cmp == 0) {

            String value1 = prop1.getValue();
            String value2 = prop2.getValue();

            // numerical sort ASC
            if (value1.matches("\\d+") && value2.matches("\\d+")) {
                cmp = Integer.parseInt(value1) - Integer.parseInt(value2);
            }
            // lexicographic sort ASC
            else {
                cmp = prop1.getValue().compareTo(prop2.getValue());
            }
        }

        return cmp;
    }
}
