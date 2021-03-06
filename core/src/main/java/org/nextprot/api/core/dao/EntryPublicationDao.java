package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.publication.PublicationDirectLink;

import java.util.List;
import java.util.Map;

public interface EntryPublicationDao {

    Map<Long, List<PublicationDirectLink>> findPublicationDirectLinks(String entryAccession);
}
