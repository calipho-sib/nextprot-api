package org.nextprot.api.core.service;

import org.nextprot.api.commons.bio.AminoAcidCode;

public interface ProteinModificationService {

    AminoAcidCode findPTMTarget(String ptmId);
}
