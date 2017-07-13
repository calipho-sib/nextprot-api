package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.IsoformSequenceInfoPeff;
import org.nextprot.api.core.service.IsoformSequenceService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.peff.PeffHeaderFormatter;
import org.nextprot.api.core.utils.peff.PeffHeaderFormatterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class IsoformSequenceServiceImpl implements IsoformSequenceService {

	@Autowired
	private IsoformService isoformService;

	@Autowired
	private TerminologyService terminologyService;

	@Override
	public IsoformSequenceInfoPeff formatSequenceInfoPeff(Entry entry, String name) {

		IsoformSequenceInfoPeff peff = new IsoformSequenceInfoPeff();

		PeffHeaderFormatter formatter = new PeffHeaderFormatterImpl(entry, isoformService.findIsoformByName(entry, name));

		peff.setIsoformAccessionFormat(formatter.formatIsoformAccession());
		peff.setProteinNameFormat(formatter.formatProteinName());
		peff.setGeneNameFormat(formatter.formatGeneName());
		peff.setNcbiTaxonomyIdentifierFormat(formatter.formatNcbiTaxonomyIdentifier());
		peff.setTaxonomyNameFormat(formatter.formatTaxonomyName());
		peff.setSequenceLengthFormat(formatter.formatSequenceLength());
		peff.setSequenceVersionFormat(formatter.formatSequenceVersion());
		peff.setEntryVersionFormat(formatter.formatEntryVersion());
		peff.setProteinEvidenceFormat(formatter.formatProteinEvidence());
		peff.setModResPsiFormat(formatter.formatModResPsi(uniprotPtmAccession -> terminologyService.findPsiModAccession(uniprotPtmAccession)));
		peff.setModResFormat(formatter.formatModRes());
		peff.setVariantSimpleFormat(formatter.formatVariantSimple());
		peff.setVariantComplexFormat(formatter.formatVariantComplex());
		peff.setProcessedMoleculeFormat(formatter.formatProcessedMolecule());

		return peff;
	}
}
