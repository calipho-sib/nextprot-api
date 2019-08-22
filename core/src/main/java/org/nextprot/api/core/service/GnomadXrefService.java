package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.DbXref;

import java.util.List;

public interface GnomadXrefService {

    List<DbXref> findGnomadDbXrefsByMaster(String entryName);
}
