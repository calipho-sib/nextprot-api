package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.Overview.History;


public interface HistoryDao {

	List<History> findHistoryByEntry(String uniqueName);
}
