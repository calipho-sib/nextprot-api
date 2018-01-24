package org.nextprot.api.core.domain;

import java.io.Serializable;

public class ProteinExistenceInferred implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static ProteinExistenceInferred NO_INFERENCE_FOUND = new ProteinExistenceInferred();

    public enum ProteinExistenceRule {

        SP_PER_01("Cannot be promoted"),
        SP_PER_02("Promote to PE1 based on proteomics data"),
        SP_PER_03("Promote to PE1 based on expression data"),
        SP_PER_04("Promote PE3 or PE4 to PE2 based on expression data"),
        SP_PER_05("Promote to PE1 based on mutagenesis data"),
        SP_PER_06("Promote to PE1 based on interaction data");

        private final String title;

        ProteinExistenceRule(String title) {

            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private ProteinExistence proteinExistence;
    private ProteinExistenceRule rule;

    private ProteinExistenceInferred() { }

    public ProteinExistenceInferred(ProteinExistence proteinExistence, ProteinExistenceRule rule) {
        this.proteinExistence = proteinExistence;
        this.rule = rule;
    }

    public static ProteinExistenceInferred noInferenceFound() {

        return NO_INFERENCE_FOUND;
    }

    public ProteinExistence getProteinExistence() {
        return proteinExistence;
    }

    public ProteinExistenceRule getRule() {
        return rule;
    }

    public boolean isInferenceFound() {
        return rule != null;
    }
}
