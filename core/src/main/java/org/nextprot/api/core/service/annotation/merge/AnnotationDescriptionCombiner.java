package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.StatementAnnotDescription;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.text.ParseException;
import java.util.logging.Logger;


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

    public AnnotationDescriptionCombiner(String geneName, Annotation annotation) {

        Preconditions.checkNotNull(annotation);
        Preconditions.checkNotNull(geneName);

        this.annotation = annotation;

        if (annotation.getTargetingIsoformsMap().isEmpty()) {

            throw new NextProtException("Cannot combine description: missing isoform mapping for annotation "+annotation.getAnnotationId());
        }

        parser = new AnnotationDescriptionParser(geneName);
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
