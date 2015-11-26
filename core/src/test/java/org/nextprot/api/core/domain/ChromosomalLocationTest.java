package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChromosomalLocationTest {

    @Test
    public void testToString() throws Exception {

        List<ChromosomalLocation> locations = newChromosomalLocations("6p22.2", "6p22.2", "6p22.2", "6p22.2", "6p22.2", "6p22.1", "6p22.2", "6p22.1", "6p22.2", "1q21.2", "6p22.1", "1q21.2", "12p12.3", "6p22.1");

        String string = ChromosomalLocation.toString(locations);

        Assert.assertEquals("1q21.2, 6p22.1, 6p22.2, 12p12.3", string);
    }

    public static List<ChromosomalLocation> newChromosomalLocations(String... locations) {

        List<ChromosomalLocation> locs = new ArrayList<>();

        Pattern pattern = Pattern.compile("^(\\d+)([pq].+)?$");

        for (String location :locations) {

            Matcher matcher = pattern.matcher(location);

            if (matcher.find()) {

                ChromosomalLocation chromosomalLocation = new ChromosomalLocation();
                chromosomalLocation.setChromosome(matcher.group(1));
                chromosomalLocation.setBand((matcher.group(2) != null) ? matcher.group(2) : "");

                locs.add(chromosomalLocation);
            }
        }

        return locs;
    }
}