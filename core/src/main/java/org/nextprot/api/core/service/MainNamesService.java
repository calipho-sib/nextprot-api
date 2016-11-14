package org.nextprot.api.core.service;

import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.service.annotation.ValidEntry;

/**
 * Extracts main names of proteins and isoforms based on isoform_identifier_view
 * 
 * @author pam
 */
public interface MainNamesService {

	Map<String,MainNames> findIsoformOrEntryMainName();

}
