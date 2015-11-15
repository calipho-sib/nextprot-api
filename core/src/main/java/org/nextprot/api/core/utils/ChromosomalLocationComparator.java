package org.nextprot.api.core.utils;

import org.nextprot.api.core.domain.ChromosomalLocation;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Comparison function that sort ChromosomalLocation according to chromosome, region, band and sub-band
 *
 * <h3>Multiple criteria</h3>
 *
 * <h3>First by chromosome</h3>
 * "1" < ... < "23" < "X" < "Y" < "MT" < "unknown"
 *
 * <h3>Second by region</h3>
 * "unknown" < "p" < "cen" < "q"
 *
 * <h3>Third by band</h3>
 * numerical order ASC
 *
 * <h3>Then by sub-band</h3>
 * numerical order ASC
 *
 * Created by fnikitin on 12/11/15.
 */
public class ChromosomalLocationComparator implements Comparator<ChromosomalLocation> {

    private final static Pattern LOCATION_PATTERN = Pattern.compile("^[^pq]*([pq]|cen)(\\d+)?(\\.\\d+)?.*");
    private final static Pattern INTEGER_PATTERN = Pattern.compile("^(\\d+)$");

    private final static int UNKNOWN_CHROMOSOME_INDEX = 26;
    private final static int UNKNOWN_REGION_INDEX = 0;

    @Override
    public int compare(ChromosomalLocation l1, ChromosomalLocation l2) {

        int[] indices1 = calcLocationIndexList(l1);
        int[] indices2 = calcLocationIndexList(l2);

        for (int i=0; i<4 ; i++) {

            if (indices1[i] != indices2[i]) {

                return indices1[i] - indices2[i];
            }
        }

        return 0;
    }

    private int[] calcLocationIndexList(ChromosomalLocation chromosomalLocation) {

        return calcLocationIndexList(chromosomalLocation.getChromosome(), chromosomalLocation.getBand());
    }

    /**
     * Calculate a list of indices of respectively:
     *
     * 1. Chromosome (1, ..., 23, X, Y, MT, unknown)
     * 2. Band decomposed in region/band/subband or "unknown"/""
     * 2.1 region (cen, p or q)
     * 2.2 band (int)
     * 2.3 sub band (int)
     *
     * For example:
     *
     * - "19p13.11" should return [18, 0, 12, 10]
     * - "19" should return [18, 3, 0, 0]
     *
     * @param chromosome
     * @return
     */
    int[] calcLocationIndexList(String chromosome, String band) {

        int[] indices = new int[4];

        // first chromosome index
        indices[0] = calcChromosomeIndex(chromosome);

        if (band != null) {

            // a band is more precisely described as: a region, optionals band and sub-band
            Matcher matcher = LOCATION_PATTERN.matcher(band);

            if (matcher.find()) {

                // second region index
                indices[1] = calcRegionIndex(matcher.group(1));

                String b = matcher.group(2);
                if (b != null) {

                    // third band index
                    indices[2] = calcBandIndex(b);
                    String sb = matcher.group(3);

                    if (sb != null) {

                        // fourth sub-band index
                        indices[3] = calcBandIndex(sb.substring(1));
                    }
                }
            }
            // "" or unknown
            else {

                // second region index
                indices[1] = calcRegionIndex("unknown");
            }
        }

        return indices;
    }

    /**
     * Calculate the ordered-index of the given ChromosomalLocation chromosome from 0 (chromosome 1) to 26 (unknown chromosome)
     *
     * @param chromosome the chromosome
     * @return the ordered-based index
     */
    int calcChromosomeIndex(String chromosome) {

        if (chromosome == null) return UNKNOWN_CHROMOSOME_INDEX;

        Matcher matcher = INTEGER_PATTERN.matcher(chromosome);

        if (matcher.find()) {

            return Integer.parseInt(matcher.group(1))-1;
        }
        else {

            switch (chromosome) {

                case "X":
                    return 23;
                case "Y":
                    return 24;
                case "MT":
                    return 25;
                case "unknown":
                    return UNKNOWN_CHROMOSOME_INDEX;
                default:
                    return -1;
            }
        }
    }

    int calcRegionIndex(String region) {

        switch (region) {

            case "p":
                return 1;
            case "cen":
                return 2;
            case "q":
                return 3;
            case "":
            case "unknown":
                return UNKNOWN_REGION_INDEX;
            default:
                return -1;
        }
    }

    int calcBandIndex(String band) {

        Matcher matcher = INTEGER_PATTERN.matcher(band);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1))-1;
        }

        return -1;
    }
}
