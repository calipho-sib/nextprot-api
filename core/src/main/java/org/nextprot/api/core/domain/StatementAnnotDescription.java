package org.nextprot.api.core.domain;

import com.google.common.collect.Lists;
import org.nextprot.api.commons.utils.StringUtils;

import java.util.*;

public class StatementAnnotDescription {

    private String ptm;
    private Set<String> enzymes;
    private boolean alternate;
    private boolean inVitro;

    public StatementAnnotDescription() {

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

    public StatementAnnotDescription combine(StatementAnnotDescription description) throws CombineException {

        StatementAnnotDescription combinedDescription = new StatementAnnotDescription();

        if (!ptm.equalsIgnoreCase(description.getPtm())) {

            throw new CombineException(this, description);
        }

        combinedDescription.setPtm(ptm);

        if (alternate || description.isAlternate()) {

            combinedDescription.setAlternate(true);
        }

        // TODO: not sure about '|| description.isInVitro()'
        if (inVitro || description.isInVitro()) {

            combinedDescription.setInVitro(true);
        }

        combinedDescription.addAllEnzymes(enzymes);
        combinedDescription.addAllEnzymes(description.getEnzymes());

        return combinedDescription;
    }

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

    public static class CombineException extends Exception {

        CombineException(StatementAnnotDescription desc, StatementAnnotDescription other) {

            super("Cannot combine description object '"+desc.format()+ "' with '"+ other.format() + "' because of different ptm names: ("+desc.getPtm() +" != "+other.getPtm()+")");
        }
    }
}
