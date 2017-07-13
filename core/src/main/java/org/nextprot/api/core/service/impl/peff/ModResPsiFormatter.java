package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.EnumSet;
import java.util.function.Function;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
public class ModResPsiFormatter extends PTMInfoFormatter {

    private final Function<String, String> uniprotModToPsi;

    public ModResPsiFormatter(Function<String, String> uniprotModToPsi) {

        super(EnumSet.of(AnnotationCategory.MODIFIED_RESIDUE, AnnotationCategory.CROSS_LINK, AnnotationCategory.LIPIDATION_SITE),
                SequenceDescriptorKey.MOD_RES_PSI);

        this.uniprotModToPsi = uniprotModToPsi;
    }

    @Override
    protected String getModAccession(Annotation annotation) {

        return uniprotModToPsi.apply(annotation.getCvTermAccessionCode());
    }
}