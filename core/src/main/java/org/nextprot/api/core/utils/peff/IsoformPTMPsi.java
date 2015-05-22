package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
public class IsoformPTMPsi extends IsoformPTM {

    // PTM-XXXX -> MOD:YYYYY
    @Deprecated
    private static Map<String, String> psiModMap = new HashMap<>();

    private static Set<AnnotationApiModel> SUPPORTED_MODELS = EnumSet.of(AnnotationApiModel.MODIFIED_RESIDUE, AnnotationApiModel.CROSS_LINK, AnnotationApiModel.LIPIDATION_SITE);

    IsoformPTMPsi(String isoformId, Annotation annotation) {

        super(isoformId, annotation, SUPPORTED_MODELS, (psiModMap.containsKey(annotation.getCvTermAccessionCode())) ?
                        psiModMap.get(annotation.getCvTermAccessionCode()) : annotation.getCvTermAccessionCode());
    }

    @Deprecated
    public static void addPsiModIdsToMap(List<Annotation> annotations, PsiModMapper mapper) {

        for (Annotation annotation : annotations) {

            if (SUPPORTED_MODELS.contains(annotation.getAPICategory())) {

                String id = annotation.getCvTermAccessionCode();
                String modId = mapper.getPsiModId(id);

                if (modId != null && !psiModMap.containsKey(id)) psiModMap.put(id, modId);
            }
        }
    }

    @Override
    public boolean isPSI() {
        return true;
    }

    public static boolean isModelSupported(AnnotationApiModel model) {

        return SUPPORTED_MODELS.contains(model);
    }
}