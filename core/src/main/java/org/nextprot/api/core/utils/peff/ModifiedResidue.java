package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * A modified residue type modification
 *
 * Created by fnikitin on 05/05/15.
 */
public class ModifiedResidue extends Modification {

    public ModifiedResidue(String isoformId, Annotation annotation) {

        super(isoformId, annotation);
    }
}