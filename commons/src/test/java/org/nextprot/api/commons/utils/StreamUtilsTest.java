package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StreamUtilsTest {

    @Test
    public void nullListShouldConvertToEmptyStream() throws Exception {

        Assert.assertEquals(0, StreamUtils.nullableListToStream(null).count());
    }

    @Test
    public void listShouldConvertToStream() throws Exception {

        Assert.assertEquals(2, StreamUtils.nullableListToStream(Arrays.asList(1, 2)).count());
    }

    @Test
    public void nullMapShouldConvertToEmptyStream() throws Exception {

        Assert.assertEquals(0, StreamUtils.nullableMapToStream(null).count());
    }

    @Test
    public void mapShouldConvertToStream() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("1", "one");
        map.put("2", "two");

        Assert.assertEquals(2, StreamUtils.nullableMapToStream(map).count());
    }
}