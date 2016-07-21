package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;

public interface IsoformMappingService {

	/**
	 * Check that the specified feature is valid on the given entry
	 *
	 * @param isoformFeature the feature to validate
	 * @param featureType feature type (variant or ptm)
	 * @param nextprotEntryAccession the entry accession number (example: NX_P01308)
	 * @return a MappedIsoformsFeatureResult
	 * @throw a NextprotException when it is not an entry accession
     */
	FeatureQueryResult validateFeature(String isoformFeature, String featureType, String nextprotEntryAccession);

	/**
	 * Compute the projections of isoform feature on other isoforms.
	 *
	 * @param isoformFeature the feature to project or propagate (can be HGVS name example: SCN11A-p.Leu1158Pro or in case of PTM: BRCA1-P-Ser988)
	 * @param featureType feature category (variant or ptm)
	 * @param nextprotEntryAccession the entry accession number (example: NX_P01308)
     * @return a MappedIsoformsFeatureResult
	 * @throw a NextprotException when it is not an entry accession
     */
	FeatureQueryResult propagateFeature(String isoformFeature, String featureType, String nextprotEntryAccession);
}
