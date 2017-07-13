package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.IsoformSequenceInfoPeff;

public interface IsoformSequenceService {

	IsoformSequenceInfoPeff formatSequenceInfoPeff(Entry entry, String isoformName);
}
