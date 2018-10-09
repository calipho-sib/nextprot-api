package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.ProteinModificationService;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ProteinModificationServiceImpl implements ProteinModificationService {

    @Autowired
	private TerminologyService terminologyService;

    @Override
    public AminoAcidCode findPTMTarget(String ptmId) {

        if (!ptmId.matches("PTM-\\d{4}")) {

            throw new NextProtException("argument of findPTMTarget() should be a PTM-XXXX id");
        }

        CvTerm cvTerm = terminologyService.findCvTermByAccession(ptmId);

        return cvTerm.getProperty("Target")
                .map(termProperty -> AminoAcidCode.valueOfAminoAcid(termProperty.getPropertyValue()))
                .orElseThrow(() -> new NextProtException("Missing amino-acid target for " + ptmId));
    }
}
