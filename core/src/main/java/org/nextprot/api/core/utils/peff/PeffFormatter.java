package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * PEFF or PSI Extended FASTA Format is an enriched FASTA format specified by the HUPO PSI (http://www.psidev.info/node/363).
 *
 * Created by fnikitin on 05/05/15.
 */
public interface PeffFormatter {

    enum PeffKey {

        MOD_RES_PSI("ModResPsi"),
        MOD_RES("ModRes"),
        PROCESSED("Processed"),
        VARIANT("Variant"),
        ;

        String name;

        PeffKey(String name) {

            this.name = name;
        }

        public String getName() {

            return name;
        }
    }

    PeffKey getPeffKey();
    String asPeffValue(Isoform isoform, Annotation... annotations);
    boolean support(AnnotationApiModel model);
}
