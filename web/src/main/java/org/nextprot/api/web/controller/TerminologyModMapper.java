package org.nextprot.api.web.controller;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TerminologyModMapper {

    private static final Log Logger = LogFactory.getLog(TerminologyModMapper.class);

    @Autowired
    private TerminologyService terminologyService;

    public String getPsiModId(String modName) {

        Preconditions.checkNotNull(modName);

        Terminology term = terminologyService.findTerminologyByAccession(modName);

        if (term == null) {
            Logger.warn("no term found for " + modName);
        }
        else {
            for (String synonym : term.getSameAs()) {

                if (synonym.matches("\\d{5}"))
                    return "MOD:" + synonym;
            }
        }

        return null;
    }
}