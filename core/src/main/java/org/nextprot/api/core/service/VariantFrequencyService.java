package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.VariantFrequency;

public interface VariantFrequencyService {

    VariantFrequency findVariantFrequencyByRSID(String RSID);
}
