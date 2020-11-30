package org.nextprot.api.core.service;

import java.util.Map;

import org.nextprot.api.core.domain.CvDatabase;

public interface SimpleService {

	Map<String,CvDatabase> getNameDatabaseMap();

}