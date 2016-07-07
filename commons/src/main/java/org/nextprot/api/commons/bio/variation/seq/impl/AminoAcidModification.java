package org.nextprot.api.commons.bio.variation.seq.impl;

import org.nextprot.api.commons.bio.variation.seq.SequenceChange;

import java.util.HashSet;
import java.util.Set;

/**
 * A modification on a single amino-acid
 *
 * Created by fnikitin on 09/07/15.
 */
public enum AminoAcidModification implements SequenceChange<AminoAcidModification> {

    // TODO: should not we had a rule for each modification to occur based on modified aa and sequence neighborhood
    Acetylation("Ac"),
    Glycosylation("carb"),
    Dimethylation("dimethyl"),
    Geranylgeranylation("ger"),
    Farnesylation("SFarn"),
    Myristoylation("myr"),
    Nitration("nitro"),
    Phosphorylation("P"),
    Palmitoylation("palm"),
    PolyADP_Ribosylation("PAR"),
    S_Nitrosation("SNO"),
    SUMOylation("sumo"),
    Ubiquitination("ubi")
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
                return Acetylation;
            case "carb":
                return Glycosylation;
            case "dimethyl":
                return Dimethylation;
            case "ger":
                return Geranylgeranylation;
            case "sfarn":
                return Farnesylation;
            case "myr":
                return Myristoylation;
            case "nitro":
                return Nitration;
            case "p":
                return Phosphorylation;
            case "palm":
                return Palmitoylation;
            case "par":
                return PolyADP_Ribosylation;
            case "sno":
                return S_Nitrosation;
            case "sumo":
                return SUMOylation;
            case "ubi":
                return Ubiquitination;
            default:
                throw new IllegalArgumentException("No enum constant "+AminoAcidModification.class.getSimpleName()+"." + name);
        }
    }
}
