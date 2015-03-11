package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

public class Base36CodecTest {

    @Test
    public void testEncodeBase36LowerLong() throws Exception {

        String code = Base36Codec.encodeBase36(Base36Codec.getLowerBound());
        Assert.assertEquals("00000000", code);
    }

    @Test
    public void testEncodeBase36MaxLong() throws Exception {


        String code = Base36Codec.encodeBase36(Base36Codec.getUpperBound()-1);
        Assert.assertEquals("ZZZZZU8V", code);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEncodeBase36MaxOutOfBound() throws Exception {

        Base36Codec.encodeBase36(Base36Codec.getUpperBound());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEncodeBase36MaxOutOfBound2() throws Exception {

        Base36Codec.encodeBase36(Base36Codec.getLowerBound() - 1);
    }

    @Test
    public void testDecodeBase36MinLong() throws Exception {


        long code = Base36Codec.decodeBase36("00000000");
        Assert.assertEquals(0, code);
    }

    @Test
    public void testDecodeBase36MinLongShorter() throws Exception {


        long code = Base36Codec.decodeBase36("0");
        Assert.assertEquals(0, code);
    }

    @Test
    public void testDecodeBase36MaxLong() throws Exception {


        long code = Base36Codec.decodeBase36("ZZZZZU8V");
        Assert.assertEquals(Base36Codec.getUpperBound() - 1, code);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testDecodeBase36OutOfBound() throws Exception {

        Base36Codec codec = new Base36Codec();

        codec.decodeBase36("ZZZZZU8W");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodeBase36TooLongString() throws Exception {

        Base36Codec.decodeBase36("000000001");
    }

    @Test(expected = NumberFormatException.class)
    public void testDecodeBase36InvalidChar() throws Exception {

        Base36Codec.decodeBase36("ZZ_TOP");
    }

    @Test
    public void testGiveNextBase36String() throws Exception {

        Base36Codec.Generator generator = new Base36Codec.Generator();

        for (int i=0 ; i<10 ; i++) {

            Assert.assertNotNull(generator.next36BaseString());
        }
    }
}