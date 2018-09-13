package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.SequenceUnicity;

import java.util.Map;
import java.util.Set;

/**
 * @author Valentine Rech de Laval
 * @since 2018-08-30
 */
public interface AntibodyUnicityService {

    SequenceUnicity getAntibpdyUnicityFromMappingIsoforms(Set<String> isoformAcs);

    Map<String,SequenceUnicity> getAntibpdyNameUnicityMap();

}
