package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

public class AnnotationPropertyComparatorTest {

    @Test
    public void testCompareByNameFirst() throws Exception {

        AnnotationPropertyComparator comparator = new AnnotationPropertyComparator();

        AnnotationProperty prop1 = mockAnnotationProperty("position", "853");
        AnnotationProperty prop2 = mockAnnotationProperty("conflict", "853");

        Assert.assertTrue(comparator.compare(prop1, prop2) > 0);
    }

    @Test
    public void testCompareThenByValueInNumericContext() throws Exception {

        AnnotationPropertyComparator comparator = new AnnotationPropertyComparator();

        AnnotationProperty prop1 = mockAnnotationProperty("position", "853");
        AnnotationProperty prop2 = mockAnnotationProperty("position", "1538989");

        Assert.assertTrue(comparator.compare(prop1, prop2) < 0);
    }

    @Test
    public void testCompareThenByValueInLexicographicContext() throws Exception {

        AnnotationPropertyComparator comparator = new AnnotationPropertyComparator();

        AnnotationProperty prop1 = mockAnnotationProperty("position", "853");
        AnnotationProperty prop2 = mockAnnotationProperty("position", "bart");

        Assert.assertTrue(comparator.compare(prop1, prop2) < 0);
    }

    @Test
    public void testSortCollection() throws Exception {

        AnnotationPropertyComparator comparator = new AnnotationPropertyComparator();

        List<AnnotationProperty> props = new ArrayList<>();

        props.add(mockAnnotationProperty("position", "853"));
        props.add(mockAnnotationProperty("position", "553"));
        props.add(mockAnnotationProperty("position", "755"));
        props.add(mockAnnotationProperty("position", "550"));
        props.add(mockAnnotationProperty("position", "757"));
        props.add(mockAnnotationProperty("position", "496"));
        props.add(mockAnnotationProperty("position", "502"));
        props.add(mockAnnotationProperty("position", "676"));
        props.add(mockAnnotationProperty("conflict type", "frameshift"));
        props.add(mockAnnotationProperty("position", "673"));
        props.add(mockAnnotationProperty("differing sequence", "2771467"));

        Collections.sort(props, comparator);

        Assert.assertEquals("frameshift", props.get(0).getValue());
        Assert.assertEquals("2771467", props.get(1).getValue());
        Assert.assertEquals("496", props.get(2).getValue());
        Assert.assertEquals("502", props.get(3).getValue());
        Assert.assertEquals("550", props.get(4).getValue());
        Assert.assertEquals("553", props.get(5).getValue());
        Assert.assertEquals("673", props.get(6).getValue());
        Assert.assertEquals("676", props.get(7).getValue());
        Assert.assertEquals("755", props.get(8).getValue());
        Assert.assertEquals("757", props.get(9).getValue());
        Assert.assertEquals("853", props.get(10).getValue());
    }

    private AnnotationProperty mockAnnotationProperty(String name, String value) {

        AnnotationProperty prop = Mockito.mock(AnnotationProperty.class);

        when(prop.getName()).thenReturn(name);
        when(prop.getValue()).thenReturn(value);

        return prop;
    }
}