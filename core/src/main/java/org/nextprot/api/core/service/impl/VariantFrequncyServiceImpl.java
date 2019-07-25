package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.VariantFrequencyDao;
import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.nextprot.api.core.service.VariantFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Variant frequency service, which provides variant frequency data given a rsID
 */
@Service
public class VariantFrequncyServiceImpl implements VariantFrequencyService {

    @Autowired
    VariantFrequencyDao variantFrequencyDao;

    @Override
    public List<VariantFrequency> findVariantFrequencyByDBSNP(String DBSNPId, AnnotationVariant annotationVariant) {
        return variantFrequencyDao.findVariantFrequency(DBSNPId, annotationVariant);
    }

    @Override
    public Map<String,List<VariantFrequency>> findVariantFrequenciesByDBSNP(Set<String> dbSNPIds) {
        return variantFrequencyDao.findVariantFrequency(dbSNPIds);
    }
}
