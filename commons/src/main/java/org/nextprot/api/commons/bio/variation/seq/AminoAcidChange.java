package org.nextprot.api.commons.bio.variation.seq;

import java.util.HashSet;
import java.util.Set;

/**
 * A modification on single amino-acid
 *
 * Created by fnikitin on 09/07/15.
 */
public enum AminoAcidChange {

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
        validNames = new HashSet<>(AminoAcidChange.values().length);
        for (AminoAcidChange mod : AminoAcidChange.values()) {

            validNames.add(mod.getName());
        }
    }
    private final String name;

    AminoAcidChange(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isValidAminoAcidChange(String name) {

        return validNames.contains(name);
    }

    public static AminoAcidChange valueOfAminoAcidChange(String name) {

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
                throw new IllegalArgumentException("No enum constant "+AminoAcidChange.class.getSimpleName()+"." + name);
        }
    }
}
