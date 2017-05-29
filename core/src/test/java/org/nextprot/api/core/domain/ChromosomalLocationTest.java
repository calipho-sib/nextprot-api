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

    @Test
    public void testFromStringUndefinedLocation() throws Exception {

        ChromosomalLocation cl = ChromosomalLocation.fromString("-");
        Assert.assertEquals("unknown", cl.getChromosome());
        Assert.assertEquals("unknown", cl.getBand());
    }

    @Test
    public void testFromStringLocation() throws Exception {

        ChromosomalLocation cl = ChromosomalLocation.fromString("4q35.1");
        Assert.assertEquals("4", cl.getChromosome());
        Assert.assertEquals("q35.1", cl.getBand());
    }

    @Test
    public void testFromStringLocation2() throws Exception {

        ChromosomalLocation cl = ChromosomalLocation.fromString("Yq11.23");
        Assert.assertEquals("Y", cl.getChromosome());
        Assert.assertEquals("q11.23", cl.getBand());
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