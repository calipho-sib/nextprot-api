package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VariantFrequencyService {

    List<VariantFrequency> findVariantFrequencyByDBSNP(String DBSNPId, AnnotationVariant variant);

    Map<String,List<VariantFrequency>> findVariantFrequenciesByDBSNP(Set<String> DBSNPIds);
}
