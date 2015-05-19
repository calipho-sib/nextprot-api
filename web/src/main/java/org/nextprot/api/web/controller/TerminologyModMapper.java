package org.nextprot.api.web.controller;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.peff.PsiModMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TerminologyModMapper implements PsiModMapper {

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

        Terminology term = terminologyService.findTerminologyByAccession(modName);

        for (String synonym : term.getSameAs()) {

            if (synonym.matches("\\d{5}")) return "MOD:"+synonym;
        }

        return null;
    }
}