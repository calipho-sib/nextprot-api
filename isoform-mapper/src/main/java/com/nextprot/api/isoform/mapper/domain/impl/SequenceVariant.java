package com.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariationHGVSFormat;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceVariant extends SequenceFeatureBase {

    public SequenceVariant(String feature) throws ParseException {
        super(feature);
    }

    @Override
    public SequenceVariationFormat newParser() {
        return new SequenceVariationHGVSFormat();
    }

    /**
     *
     *   isoshort  -> Short
     *   isolong   -> Long
     *   iso5      -> Iso 5
     *   isodelta6 -> Delta 6
     *
     *   @return null if canonical
     */
    @Override
    protected String parseIsoformName(String feature) throws ParseException {

        String featureIsoname = extractIsonameFromFeature(feature);

        // canonical
        if (featureIsoname == null) {
            return null;
        }
        else if (featureIsoname.startsWith("iso")) {

            String name = featureIsoname.substring(3);
            if (name.matches("\\d+")) {
                return "Iso "+name;
            }
            else {
                // replace back space from underscore: some isoform names contain spaces that were replaced by underscore
                // see also method formatIsoformFeatureName()
                name = name.replace("_", " ");

                // Delta6 -> Delta 6
                Pattern pat = Pattern.compile("(\\s+)(\\d+)");
                Matcher matcher = pat.matcher(name);

                if (matcher.find()) {

                    return matcher.group(1)+" "+matcher.group(2);
                }
                return name;
            }
        }

        throw new ParseException("invalid isoform name: "+featureIsoname+" (isoform name should starts with prefix 'iso')", 0);
    }

    /**
     * @return the isoform part from feature string (null if canonical)
     */
    private String extractIsonameFromFeature(String feature) {

        Preconditions.checkNotNull(feature);

        int firstIndexOfDash = feature.indexOf("-");
        int lastIndexOfDash = feature.lastIndexOf("-");

        if (firstIndexOfDash < lastIndexOfDash) {
            return feature.substring(firstIndexOfDash+1, lastIndexOfDash);
        }

        return null;
    }

    /*
        Short   -> isoShort
        Long    -> isoLong
        Iso 5   -> iso5
        Delta 6 -> isoDelta6
        GTBP-N  -> isoGTBP-N
        Chain XP32 -> isoChain_XP32
     */
    @Override
    protected String formatIsoformFeatureName(Isoform isoform) {

        String name = isoform.getMainEntityName().getName();
        StringBuilder sb = new StringBuilder();

        if (name.startsWith("Iso"))
            sb.append(name.toLowerCase().replace(" ", ""));
        else
            sb.append("iso").append(name.replace(" ", "_"));

        return sb.toString();
    }
}
