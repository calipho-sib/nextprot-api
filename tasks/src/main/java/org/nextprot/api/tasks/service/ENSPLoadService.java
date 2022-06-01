package org.nextprot.api.tasks.service;

import java.util.List;
import java.util.Map;

/**
 * ENSP load service
 */
public interface ENSPLoadService {

    List<Map<String,Object>> loadENSPSequences();
}
