package org.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariantHGVSFormat;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceVariant extends SequenceFeatureBase {

    public SequenceVariant(String feature) throws ParseException {
        super(feature);
    }

    @Override
    protected int getPivotPoint(String feature) throws ParseException {

        int index = feature.indexOf("-p.");

        if (index >= 0) {
            return index;
        }
        throw new ParseException("Cannot separate gene name from variation (missing '-p.')", 0);
    }

    @Override
    public SequenceVariationFormat newParser() {
        return new SequenceVariantHGVSFormat();
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
    protected String parseIsoformName(String geneAndIso) throws ParseException {

        String featureIsoname = extractIsoName(geneAndIso);

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
    private String extractIsoName(String feature) {

        Preconditions.checkNotNull(feature);

        int indexOfDash = feature.indexOf("-");

        if (indexOfDash >= 0) {
            return feature.substring(indexOfDash+1);
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
