package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
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
 *     if a ptm is not mapped to PSI-MOD it is collected in a list of ModResInfos
 * </p>
 *
 * Created by fnikitin on 05/05/15.
 */
public class ModResPsiFormatter extends PTMInfoFormatter {

    private final Function<String, Optional<String>> uniprotModToPsi;
    private final Function<String, Optional<String>> uniprotModToPsiName;
    private final List<Annotation> unmappedUniprotModAnnotations;

    public ModResPsiFormatter(Entry entry, String isoformAccession, Function<String, Optional<String>> uniprotModToPsi, Function<String, Optional<String>> uniprotModToPsiName,
                              List<Annotation> unmappedUniprotModAnnotations) {

        super(entry, isoformAccession, EnumSet.of(AnnotationCategory.MODIFIED_RESIDUE, AnnotationCategory.CROSS_LINK, AnnotationCategory.LIPIDATION_SITE),
                SequenceDescriptorKey.MOD_RES_PSI);

        this.uniprotModToPsi = uniprotModToPsi;
        this.uniprotModToPsiName = uniprotModToPsiName;
        this.unmappedUniprotModAnnotations = unmappedUniprotModAnnotations;
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
    protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

        String modAccession = getModAccession(annotation);

        if (modAccession.isEmpty()) {

            unmappedUniprotModAnnotations.add(annotation);
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
}