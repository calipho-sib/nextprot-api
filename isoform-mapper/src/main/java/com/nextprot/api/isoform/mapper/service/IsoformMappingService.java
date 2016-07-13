package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;

public interface IsoformMappingService {

	/**
	 * Check that the specified feature is valid on the given isoform
	 *
	 * @param isoformFeature the feature to validate on the given isoform (can be HGVS name example: SCN11A-p.Leu1158Pro or in case of PTM: BRCA1-P-Ser988)
	 * @param featureType feature type (variant or ptm)
	 * @param nextprotAccession the accession number of the isoform containing the feature to validate (if accession does
	 *                            	not contains dash, the implicit isoform is the canonical one [Example: NX_P01308 or NX_P01308-1])
	 * @return a MappedIsoformsFeatureResult
     */
	FeatureQueryResult validateFeature(String isoformFeature, String featureType, String nextprotAccession);

	/**
	 * Compute the projections of isoform feature on other isoforms.
	 *
	 * @param isoformFeature the feature to project or propagate (can be HGVS name example: SCN11A-p.Leu1158Pro or in case of PTM: BRCA1-P-Ser988)
	 * @param featureType feature category (variant or ptm)
	 * @param nextprotAccession the accession number of the isoform containing the feature to project (if accession does
	 *                            	not contains dash, the implicit isoform is the canonical one [Example: NX_P01308 or NX_P01308-1])
     * @return a MappedIsoformsFeatureResult
     */
	FeatureQueryResult propagateFeature(String isoformFeature, String featureType, String nextprotAccession);
}
