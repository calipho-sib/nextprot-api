package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class EnumDictionaryTest {

    @Test (expected = IllegalArgumentException.class)
    public void testNativeValueOf() throws Exception {

        Pair.valueOf("last");
    }

    @Test
    public void testHasName() throws Exception {

        Assert.assertTrue(Pair.hasName("last"));
    }

    @Test
    public void testValueOfName() throws Exception {

        Assert.assertEquals(Pair.SECOND, Pair.valueOfName("last"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfNameMissing() throws Exception {

        Pair.valueOf("lasts");
    }

    private enum Pair {

        FIRST, SECOND;

        private static EnumDictionary<Pair> decorator = new EnumDictionary<Pair>(Pair.class, values()) {
            @Override
            protected void updateDictionary(Map<String, Pair> dict) {
                dict.put("last", SECOND);
            }
        };

        public static boolean hasName(String name) {

            return decorator.haskey(name);
        }

        public static Pair valueOfName(String name) {

            return decorator.valueOfKey(name);
        }
    }
}