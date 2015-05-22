package org.nextprot.api.web.controller;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.peff.PsiModMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TerminologyModMapper implements PsiModMapper {

    private static final Log Logger = LogFactory.getLog(TerminologyModMapper.class);

    @Autowired
    private TerminologyService terminologyService;

    public TerminologyModMapper() {
    }

    public TerminologyModMapper(TerminologyService service) {

        Preconditions.checkNotNull(service);

        this.terminologyService = service;
    }

    // TODO: REMOVE THIS HACK - Get PSI-MOD id from domain object Annotation that will be accessible in a future release
    @Override
    public String getPsiModId(String modName) {

        Preconditions.checkNotNull(modName);

        Terminology term = findTerm(modName);

        if (term != null) {

            for (String synonym : term.getSameAs()) {

                if (synonym.matches("\\d{5}")) return "MOD:" + synonym;
            }
        }

        return null;
    }

    private Terminology findTerm(String modName) {

        Terminology term = terminologyService.findTerminologyByAccession(modName);

        if (term == null)
            Logger.warn("no term found for "+modName);
        else if (term.getSameAs() == null)
            Logger.warn("no equivalent found for "+term.getAccession());

        return term;
    }
}