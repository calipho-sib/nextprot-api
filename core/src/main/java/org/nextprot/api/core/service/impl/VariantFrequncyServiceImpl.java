package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.VariantFrequencyDao;
import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.service.VariantFrequencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Variant frequncy service, which provides variant frequency data given a rsID
 */
@Service
public class VariantFrequncyServiceImpl implements VariantFrequencyService {

    @Autowired
    VariantFrequencyDao variantFrequencyDao;

    @Override
    public VariantFrequency findVariantFrequencyByRSID(String RSID) {
        return variantFrequencyDao.findVariantFrequency(RSID);
    }
}
