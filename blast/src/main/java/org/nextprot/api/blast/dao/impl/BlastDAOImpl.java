package org.nextprot.api.blast.dao.impl;

import org.nextprot.api.blast.dao.BlastDAO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BlastDAOImpl implements BlastDAO {

    @Override
    public Map<String, String> getAllIsoformSequences() {

        return new HashMap<>();
    }
}