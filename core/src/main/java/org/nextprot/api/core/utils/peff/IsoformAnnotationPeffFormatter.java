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

    public Set<AnnotationApiModel> getSupportedApiModels() {

        return supportedApiModels;
    }

    @Override
    public boolean support(Annotation annotation) {

        return supportedApiModels.contains(annotation.getAPICategory());
    }

    @Override
    public final PeffKey getPeffKey() {

        return peffKey;
    }
}
