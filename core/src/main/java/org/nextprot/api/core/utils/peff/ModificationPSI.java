package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
public class ModificationPsi extends Modification {

    @Deprecated
    private static Map<String, String> psiModMap = new HashMap<>();

    public ModificationPsi(String isoformId, Annotation annotation) {

        super(isoformId, annotation, psiModMap.get(annotation.getCvTermAccessionCode()));
    }

    @Deprecated
    public static void addPsiModIdToMap(String modName, String psiId) {

        if (!psiModMap.containsKey(modName)) psiModMap.put(modName, psiId);
    }
}