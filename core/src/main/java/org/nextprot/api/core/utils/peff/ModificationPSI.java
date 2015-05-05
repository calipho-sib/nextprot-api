package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
class ModificationPsi extends Modification {

    public ModificationPsi(String isoformId, Annotation annotation) {

        super(isoformId, annotation, annotation.getCvTermAccessionCode());
    }
}