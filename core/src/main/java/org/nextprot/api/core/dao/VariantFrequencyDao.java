package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.VariantFrequency;

import java.util.List;

/**
 * Variant Frequency DAO interface
 */
public interface VariantFrequencyDao {

    /**
     * Returns the variant frequncy given a dbSNP Id
     * @param RSID
     * @return
     */
    VariantFrequency findVariantFrequency(String RSID);

    /**
     * Inserts a variant frequency
     * @param variantFrequency
     */
    void insertVariantFrequency(List<VariantFrequency> variantFrequency);
}
