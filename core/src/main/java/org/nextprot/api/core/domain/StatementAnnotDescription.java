package org.nextprot.api.core.domain;

import com.google.common.collect.Lists;
import org.nextprot.api.commons.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class StatementAnnotDescription {

    private String geneName;
    private String ptm;
    private Set<String> enzymeGeneNames;
    private boolean alternate;
    private boolean inVitro;

    public StatementAnnotDescription(String geneName) {

        this.enzymeGeneNames = new TreeSet<>();
        this.geneName = geneName;
    }

    public String getPtm() {
        return ptm;
    }

    public void setPtm(String ptm) {
        this.ptm = StringUtils.uppercaseFirstLetter(ptm);
    }

    public void addAllEnzymes(Collection<String> enzymes) {
        this.enzymeGeneNames.addAll(enzymes.stream()
                .map(e -> (e.equals("autocatalysis")) ? geneName : e)
                .collect(Collectors.toList()));
    }

    public Set<String> getEnzymeGeneNames() {

        return Collections.unmodifiableSet(this.enzymeGeneNames);
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

        StatementAnnotDescription combinedDescription = new StatementAnnotDescription(geneName);

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

        combinedDescription.addAllEnzymes(enzymeGeneNames);
        combinedDescription.addAllEnzymes(description.getEnzymeGeneNames());

        return combinedDescription;
    }

    public String getGeneName() {
        return geneName;
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
        if (!enzymeGeneNames.isEmpty()) {
            List<String> enzymeList = Lists.newArrayList(enzymeGeneNames.iterator());

            sb.append("; by ");
            if (enzymeGeneNames.size() == 1) {
                sb.append(byWhichEnzyme(enzymeList.get(0)));
            }
            else if (enzymeGeneNames.size() >= 2) {

                sb.append(enzymeList.get(0));

                for (int i = 1; i< enzymeGeneNames.size()-1 ; i++) {
                    sb.append(", ");
                    sb.append(byWhichEnzyme(enzymeList.get(i)));
                }
                sb.append(" and ");
                sb.append(byWhichEnzyme(enzymeList.get(enzymeGeneNames.size()-1)));
            }
        }
        if (inVitro) {
            sb.append("; in vitro");
        }
        return sb.toString();
    }

    private String byWhichEnzyme(String enzymeGeneName) {

        return (enzymeGeneName.equals(geneName)) ? "autocatalysis" : enzymeGeneName;
    }

    public static class CombineException extends Exception {

        CombineException(StatementAnnotDescription desc, StatementAnnotDescription other) {

            super("Cannot combine description object '"+desc.format()+ "' with '"+ other.format() + "' because of different ptm names: ("+desc.getPtm() +" != "+other.getPtm()+")");
        }
    }
}
