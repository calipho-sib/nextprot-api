package org.nextprot.api.commons.utils;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Generates random base-36 8-length strings
 *
 * Created by fnikitin on 13/03/15.
 */
@Service
public class Base36StringGenService implements StringGenService {

    private final Random random = new Random();

    /**
     * @return the next random base-36 long encoded string in the range ["00000000", "ZZZZZU8W"[
     */
    @Override
    public String generateString() {

        long rand = (long)(random.nextDouble()*Base36Codec.getUpperBound());

        return Base36Codec.encodeBase36(rand);
    }
}
