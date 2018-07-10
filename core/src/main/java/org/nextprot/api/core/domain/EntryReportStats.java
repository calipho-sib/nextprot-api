package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.nextprot.api.core.domain.EntryReportStats.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        ENTRY_ACCESSION,
        PROTEIN_EXISTENCE,
        IS_PROTEOMICS,
        IS_MUTAGENESIS,
        IS_ANTIBODY,
        IS_3D,
        IS_DISEASE,
        ISOFORM_COUNT,
        VARIANT_COUNT,
        PTM_COUNT,
        CURATED_PUBLICATION_COUNT,
        ADDITIONAL_PUBLICATION_COUNT,
        PATENT_COUNT,
        SUBMISSION_COUNT,
        WEB_RESOURCE_COUNT,
        ENTRY_DESCRIPTION
})
public class EntryReportStats implements Serializable {

    private static final long serialVersionUID = 2L;

    public static final String ENTRY_ACCESSION = "entryAccession";
    public static final String ENTRY_DESCRIPTION = "entryDescription";
    public static final String PROTEIN_EXISTENCE = "proteinExistence";
    public static final String IS_PROTEOMICS = "proteomics";
    public static final String IS_MUTAGENESIS = "mutagenesis";
    public static final String IS_ANTIBODY = "antibody";
    public static final String IS_3D = "3D";
    public static final String IS_DISEASE = "disease";
    public static final String ISOFORM_COUNT = "isoforms";
    public static final String VARIANT_COUNT = "variants";
    public static final String PTM_COUNT = "ptms";
    public static final String CURATED_PUBLICATION_COUNT = "curatedPublicationCount";
    public static final String ADDITIONAL_PUBLICATION_COUNT = "additionalPublicationCount";
    public static final String PATENT_COUNT = "patentCount";
    public static final String SUBMISSION_COUNT = "submissionCount";
    public static final String WEB_RESOURCE_COUNT = "webResourceCount";

    private String accession;
    private String description;

    private ProteinExistence proteinExistence;
    private Map<String, Boolean> propertyTests = new HashMap<>(4);
    private Map<String, Integer> propertyCounts = new HashMap<>(4);

    @JsonProperty(ENTRY_ACCESSION)
    public String getAccession() {
        return accession;
    }

    @JsonProperty(ENTRY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(PROTEIN_EXISTENCE)
    public ProteinExistence getProteinExistence() {
        return proteinExistence;
    }

    @JsonProperty(IS_PROTEOMICS)
    public boolean isProteomics() {

        return testProperty(IS_PROTEOMICS);
    }

    @JsonProperty(IS_MUTAGENESIS)
    public boolean isMutagenesis() {

        return testProperty(IS_MUTAGENESIS);
    }

    @JsonProperty(IS_ANTIBODY)
    public boolean isAntibody() {

        return testProperty(IS_ANTIBODY);
    }

    @JsonProperty(IS_3D)
    public boolean is3D() {

        return testProperty(IS_3D);
    }

    @JsonProperty(IS_DISEASE)
    public boolean isDisease() {

        return testProperty(IS_DISEASE);
    }

    @JsonProperty(ISOFORM_COUNT)
    public int countIsoforms() {

        return propertyCounts.get(ISOFORM_COUNT);
    }

    @JsonProperty(VARIANT_COUNT)
    public int countVariants() {

        return propertyCounts.get(VARIANT_COUNT);
    }

    @JsonProperty(PTM_COUNT)
    public int countPTMs() {

        return propertyCounts.get(PTM_COUNT);
    }

    @JsonProperty(CURATED_PUBLICATION_COUNT)
    public int countCuratedPublications() {

        return propertyCounts.get(CURATED_PUBLICATION_COUNT);
    }

    @JsonProperty(ADDITIONAL_PUBLICATION_COUNT)
    public int countAdditionalPublications() {

        return propertyCounts.get(ADDITIONAL_PUBLICATION_COUNT);
    }

    @JsonProperty(PATENT_COUNT)
    public int countPatents() {

        return propertyCounts.get(PATENT_COUNT);
    }

    @JsonProperty(SUBMISSION_COUNT)
    public int countSubmissions() {

        return propertyCounts.get(SUBMISSION_COUNT);
    }

    @JsonProperty(WEB_RESOURCE_COUNT)
    public int countWebResources() {

        return propertyCounts.get(WEB_RESOURCE_COUNT);
    }

    private boolean testProperty(String testName) {

        return propertyTests.containsKey(testName) && propertyTests.get(testName);
    }

    public int count(String countName) {

        return propertyCounts.getOrDefault(countName, 0);
    }

    public void setPropertyTest(String testName, boolean bool) {

        propertyTests.put(testName, bool);
    }

    public void setPropertyCount(String countName, int count) {

        propertyCounts.put(countName, count);
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProteinExistence(ProteinExistence proteinExistence) {
        this.proteinExistence = proteinExistence;
    }
}
