package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.Overview.History;


public interface HistoryDao {

	List<History> findHistoryByEntry(String uniqueName);
}
