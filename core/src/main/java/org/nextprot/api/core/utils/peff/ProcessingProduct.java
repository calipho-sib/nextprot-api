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
 * Created by fnikitin on 05/05/15.
 */
public class ProcessingProduct extends LocatedAnnotation {

    private final AnnotationApiModel category;

    /**
     * INITIATOR_METHIONINE
     * SIGNAL_PEPTIDE
     * MATURATION_PEPTIDE
     * MATURE_PROTEIN
     */
    private ProcessingProduct(String isoformId, Annotation annotation) {
        super(isoformId, annotation);

        category = annotation.getAPICategory();
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(getEnd()).append("|").append(category.getDbAnnotationTypeName()).append(")");
        return sb.toString();
    }

    public static String getProductsAsPeffString(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        StringBuilder sb = new StringBuilder();

        for (ProcessingProduct product : getListProcessingProduct(entry, isoform)) {

            sb.append(product.asPeff());
        }

        return sb.toString();
    }

    static List<ProcessingProduct> getListProcessingProduct(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        List<ProcessingProduct> products = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

            if (annotation.getAPICategory().isChildOf(AnnotationApiModel.PROCESSING_PRODUCT))
                products.add(new ProcessingProduct(isoform.getUniqueName(), annotation));
        }

        Collections.sort(products);

        return products;
    }
}
