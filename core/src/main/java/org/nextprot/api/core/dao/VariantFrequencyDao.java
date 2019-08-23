package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Variant Frequency DAO interface
 */
public interface VariantFrequencyDao {

    /**
     * Returns the variant frequncy given a dbSNP Id
     * @param dbSNPId
     * @return List of variant frequencies
     */
    List<VariantFrequency> findVariantFrequency(String dbSNPId, AnnotationVariant variant);

    /**
     * Returns a list of variant frequencies given a dbSNP Id list
     * @param dbSNPId
     * @return List of variant frequencies
     */
    Map<String, List<VariantFrequency>> findVariantFrequency(Set<String> dbSNPId);
}
