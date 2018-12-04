package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

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
public class PEFFModResPsi extends PEFFPTMInformation {

    private final Function<String, Optional<String>> uniprotModToPsi;
    private final Function<String, Optional<String>> uniprotModToPsiName;
    private final List<Annotation> unmappedUniprotModAnnotations;

    public PEFFModResPsi(String isoformAccession, List<Annotation> isoformAnnotations,
                         Function<String, Optional<String>> uniprotModToPsi,
                         Function<String, Optional<String>> uniprotModToPsiName,
                         List<Annotation> unmappedUniprotModAnnotations) {

        super(isoformAccession, isoformAnnotations, EnumSet.of(AnnotationCategory.MODIFIED_RESIDUE,
                AnnotationCategory.CROSS_LINK, AnnotationCategory.DISULFIDE_BOND, AnnotationCategory.LIPIDATION_SITE),
                Key.MOD_RES_PSI);

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

        if (annotation.getAPICategory() == AnnotationCategory.DISULFIDE_BOND) {

            formatDisulfideBond(annotation, sb);
        }
        else {
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

    private void formatDisulfideBond(Annotation disulfideBondAnnotation, StringBuilder sb) {

        Integer startPos = disulfideBondAnnotation.getStartPositionForIsoform(isoformAccession);
        Integer endPos = disulfideBondAnnotation.getEndPositionForIsoform(isoformAccession);

        sb
                .append("(")
                .append((startPos != null) ? startPos : "?")
                .append("|MOD:00798|")
                .append("half cystine")
                .append(")")
                .append("(")
                .append((endPos != null) ? endPos : "?")
                .append("|MOD:00798|")
                .append("half cystine")
                .append(")")
        ;
    }
}