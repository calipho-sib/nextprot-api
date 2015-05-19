package org.nextprot.api.web.service.impl;

import org.apache.velocity.Template;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.utils.peff.IsoformPTMPsi;
import org.nextprot.api.core.utils.peff.PsiModMapper;

import java.io.IOException;
import java.io.Writer;

/**
 * Export entries sequence in PEFF (extended FASTA) format
 *
 * Created by fnikitin on 28/04/15.
 */
public class PeffStreamExporter extends NPStreamExporter {

    private final Template template;

    @Deprecated
    private TerminologyMapper terminologyMapper = new TerminologyMapper();

    // TODO: REMOVE THIS HACK - Get PSI-MOD id from domain object Annotation that will be accessible in a future release
    @Deprecated
    private class TerminologyMapper implements PsiModMapper {

        @Override
        public String getPsiModId(String modName) {

            Terminology term = terminologyService.findTerminologyByAccession(modName);

            for (String synonym : term.getSameAs()) {

                if (synonym.matches("\\d{5}")) return "MOD:"+synonym;
            }

            return null;
        }
    }

    public PeffStreamExporter() {

        template = velocityConfig.getVelocityEngine().getTemplate("peff/entry.peff.vm");
    }

    @Override
    protected void exportStream(String entryName, Writer writer, String viewName) throws IOException {

        streamWithVelocityTemplate(template, entryName, writer, "isoform", "entry");
    }

    @Override
    protected void handleEntry(Entry entry) {

        IsoformPTMPsi.addPsiModIdsToMap(entry.getAnnotations(), terminologyMapper);
    }
}
