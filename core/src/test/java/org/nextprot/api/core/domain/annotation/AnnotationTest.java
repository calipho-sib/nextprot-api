package org.nextprot.api.core.domain.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.utils.annot.AnnotationUtilsTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotationTest {

    @Test
    public void annotationPropertiesShouldBeSorted() throws Exception {

        Annotation annotation = new Annotation();

        List<AnnotationProperty> props = new ArrayList<>();

        props.add(newAnnotationProperty(0, "position", "853"));
        props.add(newAnnotationProperty(1, "position", "553"));
        props.add(newAnnotationProperty(2, "position", "755"));
        props.add(newAnnotationProperty(3, "position", "550"));
        props.add(newAnnotationProperty(4, "position", "757"));
        props.add(newAnnotationProperty(5, "position", "496"));
        props.add(newAnnotationProperty(6, "position", "502"));
        props.add(newAnnotationProperty(7, "position", "676"));
        props.add(newAnnotationProperty(8, "conflict type", "frameshift"));
        props.add(newAnnotationProperty(9, "position", "673"));
        props.add(newAnnotationProperty(10, "differing sequence", "2771467"));

        annotation.addProperties(props);

        assertExpectedSortedProperties(annotation,
                newAnnotationProperty(8, "conflict type", "frameshift"),
                newAnnotationProperty(10, "differing sequence", "2771467"),
                newAnnotationProperty(5, "position", "496"),
                newAnnotationProperty(6, "position", "502"),
                newAnnotationProperty(3, "position", "550"),
                newAnnotationProperty(1, "position", "553"),
                newAnnotationProperty(9, "position", "673"),
                newAnnotationProperty(7, "position", "676"),
                newAnnotationProperty(2, "position", "755"),
                newAnnotationProperty(4, "position", "757"),
                newAnnotationProperty(0, "position", "853")
        );
    }

    public static AnnotationProperty newAnnotationProperty(long annotationId, String name, String value) {

        return AnnotationUtilsTest.newAnnotationProperty(annotationId, null, name, value, null);
    }

    public static void assertExpectedSortedProperties(Annotation annotation, AnnotationProperty... expectedProperties) {

        Collection<AnnotationProperty> properties = annotation.getProperties();

        Assert.assertEquals(11, annotation.getProperties().size());

        int i=0;
        for (AnnotationProperty property : properties) {

            Assert.assertEquals(expectedProperties[i++], property);
        }
    }
}