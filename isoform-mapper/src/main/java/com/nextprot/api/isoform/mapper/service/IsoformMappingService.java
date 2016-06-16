package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.IsoformFeatureMapping;
import org.nextprot.api.commons.constants.AnnotationCategory;

public interface IsoformMappingService {

	/**
	 * @param featureName can be HGVS name example: SCN11A-p.Leu1158Pro or in case of PTM: BRCA1-P-Ser988).
	 * @param annotationCategory annotation category (VARIANT or PTM)
	 * @param nextprotAccession if not dash check the canonical else check specified iso (Example: NX_P01308 or NX_P01308-1)
	 * @param propagate if true
	 *
	 * @return an IsoformFeatureMapping
     */
	IsoformFeatureMapping validateFeature(String featureName, AnnotationCategory annotationCategory, String nextprotAccession, boolean propagate);
}
