package org.nextprot.api.core.service;

import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.phenotypes.PhenotypeAnnotation;
import org.nextprot.api.core.service.annotation.ValidEntry;


public interface PhenotypeService {

	Map<String, List<PhenotypeAnnotation>> findPhenotypeAnnotations(@ValidEntry String entryName);

}
