package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public class Variation extends LocatedAnnotation {

    private final String variant;

    public Variation(String isoformId, Annotation annotation) {

        super(isoformId, annotation);

        variant = annotation.getVariant().getVariant();
    }

    public String getVariant() {

        return variant;
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(getEnd()).append("|").append(variant).append(")");

        return sb.toString();
    }

    /**
     * Get all variants of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
     *
     * @param entry the entry to find variant from
     * @param isoform the isoform to find variant of
     * @return a list of Annotation of type VARIANT as PEFF format
     */
    public static String getVariantsAsPeffString(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        StringBuilder sb = new StringBuilder();

        for (Variation variation : getListVariant(entry, isoform)) {

            sb.append(variation.asPeff());
        }

        return sb.toString();
    }

    static List<Variation> getListVariant(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        List<Variation> variations = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

            if (annotation.getAPICategory() == AnnotationApiModel.VARIANT)
                variations.add(new Variation(isoform.getUniqueName(), annotation));
        }

        Collections.sort(variations);

        return variations;
    }
}