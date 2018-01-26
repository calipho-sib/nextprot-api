package org.nextprot.api.core.dao;

import org.nextprot.api.core.domain.EntryProperties;

public interface EntryPropertiesDao {

	EntryProperties findEntryProperties(String uniqueName);
}
