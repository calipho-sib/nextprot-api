package com.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariationHGVSFormat;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nextprot.api.commons.utils.StringUtils.capitalizeFirstLetter;

public class SequenceVariant extends SequenceFeatureBase {

    public SequenceVariant(String feature) throws ParseException {
        super(feature);
    }

    @Override
    public SequenceVariationFormat newParser() {
        return new SequenceVariationHGVSFormat();
    }

    @Override
    protected String parseIsoformName(String feature) throws ParseException {

        return convertToNextprotIsoformName(extractIsoformNameFromFeature(feature));
    }

    private String extractIsoformNameFromFeature(String feature) {

        Preconditions.checkNotNull(feature);

        int firstIndexOfDash = feature.indexOf("-");
        int lastIndexOfDash = feature.lastIndexOf("-");

        if (firstIndexOfDash < lastIndexOfDash) {
            return feature.substring(firstIndexOfDash+1, lastIndexOfDash);
        }

        return "";
    }

    /*
        isoshort  -> Short
        isolong   -> Long
        iso5      -> Iso 5
        isodelta6 -> Delta 6

        or empty if canonical
     */
    private String convertToNextprotIsoformName(String featureIsoformName) throws ParseException {

        // canonical
        if (featureIsoformName.isEmpty()) {
            return featureIsoformName;
        }
        else if (featureIsoformName.startsWith("iso")) {

            String name = featureIsoformName.substring(3);
            if (name.matches("\\d+")) {
                return "Iso "+name;
            }
            else {
                // delta6 -> Delta6
                name = capitalizeFirstLetter(name);

                // Delta6 -> Delta 6
                Pattern pat = Pattern.compile("(\\s+)(\\d+)");
                Matcher matcher = pat.matcher(name);

                if (matcher.find()) {

                    return matcher.group(1)+" "+matcher.group(2);
                }
            }
        }

        throw new ParseException("invalid isoform name: "+featureIsoformName, 0);
    }

    /*
        Short   -> isoshort
        Long    -> isolong
        Iso 5   -> iso5
        Delta 6 -> isodelta6
     */
    protected String formatIsoformFeatureName(Isoform isoform) {

        String name = isoform.getMainEntityName().getName();
        StringBuilder sb = new StringBuilder();

        if (!name.startsWith("Iso")) sb.append("iso");
        sb.append(name.toLowerCase().replace(" ", ""));

        return sb.toString();
    }
}
