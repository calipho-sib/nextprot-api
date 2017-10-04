package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * <p>
 *     if a ptm is not mapped to PSI-MOD
 * </p>
 *
 * Created by fnikitin on 05/05/15.
 */
public class ModResPsiFormatter extends PTMInfoFormatter {

    private final Function<String, Optional<String>> uniprotModToPsi;
    private final Function<String, Optional<String>> uniprotModToPsiName;
    private final List<ModResInfos> unmappedUniprotMods;

    public ModResPsiFormatter(Function<String, Optional<String>> uniprotModToPsi, Function<String, Optional<String>> uniprotModToPsiName,
                              List<ModResInfos> unmappedUniprotMods) {

        super(EnumSet.of(AnnotationCategory.MODIFIED_RESIDUE, AnnotationCategory.CROSS_LINK, AnnotationCategory.LIPIDATION_SITE),
                SequenceDescriptorKey.MOD_RES_PSI);

        this.uniprotModToPsi = uniprotModToPsi;
        this.uniprotModToPsiName = uniprotModToPsiName;
        this.unmappedUniprotMods = unmappedUniprotMods;
    }

    @Override
    protected String getModAccession(Annotation annotation) {

        return uniprotModToPsi.apply(annotation.getCvTermAccessionCode()).orElse("");
    }

    @Override
    protected String getModName(Annotation annotation) {

        return uniprotModToPsiName.apply(annotation.getCvTermAccessionCode()).orElse("");
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        String modAccession = getModAccession(annotation);
        Integer start = annotation.getStartPositionForIsoform(isoformAccession);

        if (modAccession.isEmpty()) {

            unmappedUniprotMods.add(new ModResInfos(start,
                    annotation.getCvTermAccessionCode(),
                    annotation.getCvTermName())
            );
        } else {
            sb
                    .append("(")
                    .append(annotation.getStartPositionForIsoform(isoformAccession))
                    .append("|")
                    .append(modAccession)
                    .append("|")
                    .append(getModName(annotation))
                    .append(")")
            ;
        }
    }

    public static class ModResInfos {

        private final Integer start;
        private final String accession;
        private final String name;

        public ModResInfos(Integer start, String accession, String name) {
            this.start = start;
            this.accession = accession;
            this.name = name;
        }

        public Integer getStart() {
            return start;
        }

        public String getAccession() {
            return accession;
        }

        public String getName() {
            return name;
        }
    }
}