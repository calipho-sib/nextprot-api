package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
class IsoformPTMPsiPeffFormatter extends IsoformPTMPeffFormatter {

    public IsoformPTMPsiPeffFormatter() {

        super(EnumSet.of(AnnotationApiModel.MODIFIED_RESIDUE, AnnotationApiModel.CROSS_LINK, AnnotationApiModel.LIPIDATION_SITE), PeffKey.MOD_RES_PSI);
    }

    @Override
    protected String getModName(Annotation annotation) {

        return annotation.getCvTermAccessionCode();
        //return (psiModMap.containsKey(annotation.getCvTermAccessionCode())) ? psiModMap.get(annotation.getCvTermAccessionCode()) : "";
    }
}