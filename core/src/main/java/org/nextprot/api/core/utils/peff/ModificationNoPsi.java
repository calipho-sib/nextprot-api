package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * A Modified residue without PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
class ModificationNoPsi extends Modification {

    public ModificationNoPsi(String isoformId, Annotation annotation) {

        super(isoformId, annotation, annotation.getCvTermName());
    }
}