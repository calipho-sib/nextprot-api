package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.nextprot.api.commons.utils.StringUtils;

import java.util.*;
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

    private final Parser parser;

    public AnnotationDescriptionCombiner() {

        parser = new Parser();
    }

    public String combine(String firstDescription, String secondDescription) {

        Description desc1 = parser.parse(firstDescription);
        Description desc2 = parser.parse(secondDescription);

        if (desc1 != null && desc2 != null) {
            return desc1.combine(desc2).format();
        }
        LOGGER.warning("Could not find rule to modify description "+firstDescription+" with description "+secondDescription + " (appending ; by enzyme name)");
        return firstDescription;
    }

    static class Description {

        private String ptm;
        private Set<String> enzymes;
        private boolean alternate;
        private boolean inVitro;

        public Description() {

            this.enzymes = new TreeSet<>();
        }

        public String getPtm() {
            return ptm;
        }

        public void setPtm(String ptm) {
            this.ptm = StringUtils.uppercaseFirstLetter(ptm);
        }

        public void addAllEnzymes(Collection<String> enzymes) {
            this.enzymes.addAll(enzymes);
        }

        public Set<String> getEnzymes() {

            return Collections.unmodifiableSet(this.enzymes);
        }

        public boolean isAlternate() {
            return alternate;
        }

        public void setAlternate(boolean alternate) {
            this.alternate = alternate;
        }

        public boolean isInVitro() {
            return inVitro;
        }

        public void setInVitro(boolean inVitro) {
            this.inVitro = inVitro;
        }

        public Description combine(Description description) {

            Description combinedDescription = new Description();

            if (!ptm.equals(description.getPtm())) {

                throw new IllegalStateException("Different ptms to combine: cannot combine description object '"+this.format()+ "' with '"+ description.format() + "' ("+ptm +" != "+description.getPtm()+")");
            }

            combinedDescription.setPtm(ptm);

            if (alternate || description.isAlternate()) {

                combinedDescription.setAlternate(true);
            }

            if (inVitro) {

                combinedDescription.setInVitro(true);
            }

            combinedDescription.addAllEnzymes(enzymes);
            combinedDescription.addAllEnzymes(description.getEnzymes());

            return combinedDescription;
        }

        // TODO
        public String format() {

            StringBuilder sb = new StringBuilder();

            if (ptm == null) {
                throw new IllegalStateException("missing ptm");
            }

            sb.append(ptm);
            if (alternate) {
                sb.append("; alternate");
            }
            if (!enzymes.isEmpty()) {
                List<String> enzymeList = Lists.newArrayList(enzymes.iterator());

                sb.append("; by ");
                if (enzymes.size() == 1) {
                    sb.append(enzymeList.get(0));
                }
                else if (enzymes.size() >= 2) {

                    sb.append(enzymeList.get(0));

                    for (int i=1; i<enzymes.size()-1 ; i++) {
                        sb.append(", ");
                        sb.append(enzymeList.get(i));
                    }
                    sb.append(" and ");
                    sb.append(enzymeList.get(enzymes.size()-1));
                }
            }
            if (inVitro) {
                sb.append("; in vitro");
            }
            return sb.toString();
        }


    }

    static class Parser {

        public Description parse(String description) {

            Description desc = new Description();

            LinkedList<String> fields = new LinkedList<>();
            Splitter
                    .onPattern(";")
                    .trimResults()
                    .omitEmptyStrings()
                    .split(description)
                    .forEach(field -> fields.add(field));


            // the ptm name: consume the first field
            desc.setPtm(fields.remove());

            if (!fields.isEmpty()) {

                // optionally consume for alternate field
                if (fields.get(0).matches("alternate")) {
                    desc.setAlternate(true);
                    fields.remove();
                }

                if (!fields.isEmpty()) {
                    // the enzymes
                    List<String> enzymes = Lists.newArrayList(Splitter
                            .onPattern("([,]|and|by)")
                            .trimResults()
                            .omitEmptyStrings()
                            .split(fields.get(0)));

                    desc.addAllEnzymes(enzymes);
                    fields.remove();
                }
            }

            return desc;
        }
    }
}
