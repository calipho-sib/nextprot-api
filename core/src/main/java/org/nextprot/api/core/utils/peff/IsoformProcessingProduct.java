package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;

/**
 * Created by fnikitin on 05/05/15.
 */
public class IsoformProcessingProduct extends IsoformAnnotation {

    private final AnnotationApiModel model;
    private static final Map<AnnotationApiModel, String> PSI_PEFF_MAP;

    static {

        PSI_PEFF_MAP = new HashMap<>();

        PSI_PEFF_MAP.put(AnnotationApiModel.SIGNAL_PEPTIDE, "SIGNAL");
        PSI_PEFF_MAP.put(AnnotationApiModel.MATURATION_PEPTIDE, "PEPTIDE");
        PSI_PEFF_MAP.put(AnnotationApiModel.MATURE_PROTEIN, "PROPEP");
        PSI_PEFF_MAP.put(AnnotationApiModel.PEROXISOME_TRANSIT_PEPTIDE, "TRANSIT");
        PSI_PEFF_MAP.put(AnnotationApiModel.MITOCHONDRIAL_TRANSIT_PEPTIDE, "TRANSIT");
    }

    IsoformProcessingProduct(String isoformId, Annotation annotation) {

        super(isoformId, annotation, PSI_PEFF_MAP.keySet());

        model = annotation.getAPICategory();
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(getEnd()).append("|").append(PSI_PEFF_MAP.get(model)).append(")");
        return sb.toString();
    }

    public static String getProductsAsPeffString(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        StringBuilder sb = new StringBuilder();

        for (IsoformProcessingProduct product : getListProcessingProduct(entry, isoform)) {

            sb.append(product.asPeff());
        }

        return sb.toString();
    }

    static List<IsoformProcessingProduct> getListProcessingProduct(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        List<IsoformProcessingProduct> products = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

            if (PSI_PEFF_MAP.containsKey(annotation.getAPICategory()))
                products.add(new IsoformProcessingProduct(isoform.getUniqueName(), annotation));
        }

        Collections.sort(products);

        return products;
    }
}
