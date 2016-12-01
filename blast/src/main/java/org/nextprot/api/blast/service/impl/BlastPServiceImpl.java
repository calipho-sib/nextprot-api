package org.nextprot.api.blast.service.impl;

import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.domain.BlastPRunner;
import org.nextprot.api.blast.service.BlastPService;
import org.springframework.stereotype.Service;

@Service
public class BlastPServiceImpl implements BlastPService {

    @Override
    public String runBlastP(BlastPConfig config, String query) {

        return new BlastPRunner(config).run(query);
    }
}
