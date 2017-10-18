package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class EnumDictionaryTest {

    @Test
    public void testValueOfName() throws Exception {

        Assert.assertEquals(Pair.SECOND, Pair.valueOfName("last"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNativeValueOf() throws Exception {

        Pair.valueOf("last");
    }

    private enum Pair {

        FIRST, SECOND;

        private static EnumDictionary<Pair> decorator = new EnumDictionary<Pair>(Pair.class, values()) {
            @Override
            protected void updateEnumMap(Map<String, Pair> map) {
                map.put("last", SECOND);
            }
        };

        public static boolean hasName(String name) {

            return decorator.hasName(name);
        }

        public static Pair valueOfName(String name) {

            return decorator.valueOfName(name);
        }
    }
}