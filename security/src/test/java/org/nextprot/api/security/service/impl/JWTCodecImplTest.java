package org.nextprot.api.security.service.impl;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class JWTCodecImplTest {

    @Test
    public void testEncodeJWT() throws Exception {

        JWTCodecImpl codec = new JWTCodecImpl();

        Map map = Maps.newHashMap();
        map.put("id", String.valueOf(2));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));

        String token = codec.encodeJWT(map, 0);
        System.out.println(token);
        //Assert.fail("todo test");
    }

    @Test
    public void testDecodeJWT() throws Exception {

        Assert.fail("todo test");
    }
}