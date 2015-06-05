package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Annotation located on isoform formattable in PEFF specified by the HUPO PSI (PubMed:19132688)
 *
 * Created by fnikitin on 05/05/15.
 */
abstract class IsoformAnnotationPeffFormatter implements PeffFormatter {

    private static final Map<AnnotationApiModel, PeffFormatter> map = new HashMap<>();

    static {
        // create and register unique instance of formatters
        new IsoformPTMPsiPeffFormatter();
        new IsoformPTMNoPsiPeffFormatter();
        new DisulfideBondPeffFormatter();
        new IsoformVariationPeffFormatter();
        new IsoformProcessingProductPeffFormatter();
    }

    protected final Set<AnnotationApiModel> supportedApiModels;
    protected final PeffKey peffKey;

    protected IsoformAnnotationPeffFormatter(Set<AnnotationApiModel> supportedApiModels, PeffKey peffKey) {

        Preconditions.checkNotNull(supportedApiModels);
        Preconditions.checkNotNull(peffKey);

        this.supportedApiModels = supportedApiModels;
        this.peffKey = peffKey;

        for (AnnotationApiModel model : supportedApiModels) {

            map.put(model, this);
        }
    }

    public static PeffFormatter getFormatter(Annotation annotation) {

        return map.get(annotation.getAPICategory());
    }

    public Set<AnnotationApiModel> getSupportedApiModels() {

        return supportedApiModels;
    }

    @Override
    public boolean support(AnnotationApiModel model) {

        return supportedApiModels.contains(model);
    }

    @Override
    public final PeffKey getPeffKey() {

        return peffKey;
    }


}
