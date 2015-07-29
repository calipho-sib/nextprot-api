package org.nextprot.api.core.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

/**
 * Created by fnikitin on 08/07/15.
 */
public class AnnotationDAOImplTest {

    @Test
    public void testSetPropertyNameValue() throws Exception {

        AnnotationProperty property = new AnnotationProperty();

        AnnotationDAOImpl.setPropertyNameValue(property, "mutation", "blablabla");

        Assert.assertEquals("blablabla", property.getValue());
    }

    @Test
    public void testSetMutationPropertyNameValue() throws Exception {

        AnnotationProperty property = new AnnotationProperty();

        AnnotationDAOImpl.setPropertyNameValue(property, "mutation AA", "p.R54C");

        Assert.assertNotEquals("p.R54C", property.getValue());
    }
}