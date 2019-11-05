package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.annotation.Annotation;
import java.util.List;

public interface VariantFrequencyService {

    void addFrequencyEvidences(String entryName, List<Annotation> annotations);
}
