package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.StatementAnnotDescription;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntityNameService;
import org.nextprot.api.core.utils.IsoformUtils;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import static org.nextprot.api.core.domain.Overview.EntityNameClass.GENE_NAMES;

/*
Regles pour les différents cas:

"" (aucun comment existant ) -> rajouter "; by BEDKINASE" à la description (pax ex "Phosphoserine" devient Phosphoserine; by CAMK1")
" alternate" -> rajouter "; by BEDKINASE"
" alternate; by AURKB, AURKC and RPS6KA5" -> Si BEDKINASE n'est pas dans les 3, rajouter BEDKINASE dans la liste
" alternate; by RPS6KA5" -> Si BEDKINASE n'est pas RPS6KA5, rajouter BEDKINASE dans la liste
" by ABL" -> Si BEDKINASE n'est pas ABL, rajouter BEDKINASE à sa place alphabetique dans la liste
" by autocatalysis"  Si BEDKINASE n'est pas l'entrée contenant la ptm, la rajouter à la liste
" by ABL1 and autocatalysis" -> Si BEDKINASE n'est ni ABL1 ni l'entrée contenant la ptm, la rajouter à la liste
" by ABL; in vitro" -> Si BEDKINASE est ABL supprimer le "in vitro" sinon rajouter BEDKINASE et laisser ABL en dernier collé au in vitro ?
" by MARK1; in PHF-tau" -> même regle que "in vitro", le préexistant doit resté collé à la note ?
" by ATM or ATR" -> Si BEDKINASE est ATM supprimer ATR et vice-versa, sinon rajouter BEDKINASE en premier
" by PHK; in form phosphorylase A" -> même regle que "in vitro", le préexistant doit resté collé à la note ?
" by viral VacV B1 kinase" -> rajouter BEDKINASE
" in form 4-P and form 5-P" -> rajouter 'by BEDKINASE'
" in isoform 2" -> rajouter 'by BEDKINASE
" in mitosis" -> 'by BEDKINASE'
" in vitro" -> rajouter 'by BEDKINASE' et supprimer "in vitro" ?
 */
public class AnnotationDescriptionCombiner {

    private static final Logger LOGGER = Logger.getLogger(AnnotationDescriptionCombiner.class.getName());

    private final Annotation annotation;
    private final AnnotationDescriptionParser parser;

    public AnnotationDescriptionCombiner(Annotation annotation, EntityNameService entityNameService) {

        Preconditions.checkNotNull(annotation);
        Preconditions.checkNotNull(entityNameService);

        this.annotation = annotation;

        if (annotation.getTargetingIsoformsMap().isEmpty()) {

            throw new NextProtException("Cannot combine description: missing isoform mapping for annotation "+annotation.getAnnotationId());
        }

        String entryAccession = IsoformUtils.findEntryAccessionFromIsoformAccession(annotation.getTargetingIsoformsMap().keySet().iterator().next());

        List<EntityName> geneNames = entityNameService.findNamesByEntityNameClass(entryAccession, GENE_NAMES);

        if (geneNames.isEmpty()) {

            throw new NextProtException("Cannot combine description: missing gene names for annotation "+annotation.getAnnotationId()
                    +", entry accession="+entryAccession);
        }

        parser = new AnnotationDescriptionParser(geneNames.stream()
                        .filter(entityName -> entityName.isMain())
                        .map(entityName -> entityName.getName())
                        .findFirst().orElseThrow(() -> new NextProtException("Cannot combine description for annotation "+annotation.getAnnotationId()
                            +", entry accession=" + entryAccession+": missing main gene name")));
    }

    /**
     * Combine descriptions together and format
     * @param firstDescription the first description
     * @param secondDescription the second description
     * @return a formatted combination of both descriptions or the first one if not possible
     */
    public String combine(String firstDescription, String secondDescription) {

        try {
            StatementAnnotDescription desc1 = parser.parse(firstDescription);
            StatementAnnotDescription desc2 = parser.parse(secondDescription);

            return desc1.combine(desc2).format();
        } catch (ParseException | StatementAnnotDescription.CombineException e) {

            LOGGER.warning("Warning for annotation "+annotation.getUniqueName()+": keeping description "+firstDescription+": "+ e.getMessage());
            return firstDescription;
        }
    }
}
