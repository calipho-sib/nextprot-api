package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioComplexTest {

    @Test
    public void test() {

        BioComplex bioComplex = new BioComplex();

        bioComplex.add(new BioIsoform());
        bioComplex.add(new BioIsoform());
        bioComplex.add(new BioObjectExternal(BioObject.Kind.CHEMICAL));

        Assert.assertEquals(3, bioComplex.size());
        Assert.assertEquals(BioObject.Kind.GROUP, bioComplex.getKind());
    }
}