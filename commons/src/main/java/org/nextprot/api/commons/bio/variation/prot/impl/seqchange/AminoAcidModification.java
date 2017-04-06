package org.nextprot.api.commons.bio.variation.prot.impl.seqchange;

import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.util.HashSet;
import java.util.Set;

/**
 * A modification on a single amino-acid
 *
 * Created by fnikitin on 09/07/15.
 */
public enum AminoAcidModification implements SequenceChange<AminoAcidModification> {

    // TODO: should not we had a rule for each modification to occur based on modified aa and sequence neighborhood
    ACETYLATION("Ac"),
    GLYCOSYLATION("carb"),
    DIMETHYLATION("dimethyl"),
    GERANYLGERANYLATION("ger"),
    FARNESYLATION("SFarn"),
    MYRISTOYLATION("myr"),
    NITRATION("nitro"),
    PHOSPHORYLATION("P"),
    PALMITOYLATION("palm"),
    POLY_ADP_RIBOSYLATION("PAR"),
    S_NITROSATION("SNO"),
    SUMOYLATION("sumo"),
    UBIQUITINATION("ubi")
    ;

    private final static Set<String> validNames;

    static {
        validNames = new HashSet<>(AminoAcidModification.values().length);
        for (AminoAcidModification mod : AminoAcidModification.values()) {

            validNames.add(mod.getName());
        }
    }
    private final String name;

    AminoAcidModification(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public AminoAcidModification getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return Type.PTM;
    }

    public static boolean isValidAminoAcidModification(String name) {

        return validNames.contains(name);
    }

    public static AminoAcidModification valueOfAminoAcidModification(String name) {

        switch (name.toLowerCase()) {
            case "ac":
                return ACETYLATION;
            case "carb":
                return GLYCOSYLATION;
            case "dimethyl":
                return DIMETHYLATION;
            case "ger":
                return GERANYLGERANYLATION;
            case "sfarn":
                return FARNESYLATION;
            case "myr":
                return MYRISTOYLATION;
            case "nitro":
                return NITRATION;
            case "p":
                return PHOSPHORYLATION;
            case "palm":
                return PALMITOYLATION;
            case "par":
                return POLY_ADP_RIBOSYLATION;
            case "sno":
                return S_NITROSATION;
            case "sumo":
                return SUMOYLATION;
            case "ubi":
                return UBIQUITINATION;
            default:
                throw new IllegalArgumentException("No enum constant "+AminoAcidModification.class.getSimpleName()+"." + name);
        }
    }
}
