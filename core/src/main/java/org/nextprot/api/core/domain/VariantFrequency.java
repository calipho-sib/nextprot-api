package org.nextprot.api.core.domain;

public class VariantFrequency {

    /*
    Data source
     */
    private String source;

    /*
    Chromosome
     */
    private String chromosome;
    private int chromosomePosition;
    private String gnomadAccession;

    /*
    DBSNP id
     */
    private String dbsnpId;

    /*
    Frequency
     */
    private int alleleCount;
    private int allelNumber;
    private double allelFrequency;
    private int homozygoteCount;

    /*
    On the Gene
     */
    private String originalNucleotide;
    private String variantNucleotide;
    private String variantType;
    private String geneName;

    /*
    Ensemble identifiers
     */
    private String ensg;
    private String enst;
    private String ensp;

    /*
    On the protein
     */
    private String originalAminoAcid;
    private String variantAminoAcid;
    private int isoformPosition;
    private String uniprotAccession;

    public void setSource(String source) {
        this.source = source;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public void setGnomadAccession(String gnomadAccession) {
        this.gnomadAccession = gnomadAccession;
    }

    public void setChromosomePosition(int chromosomePosition) {
        this.chromosomePosition = chromosomePosition;
    }

    public void setDbsnpId(String dbsnpId) {
        this.dbsnpId = dbsnpId;
    }

    public void setAlleleCount(int alleleCount) {
        this.alleleCount = alleleCount;
    }

    public void setAllelNumber(int allelNumber) {
        this.allelNumber = allelNumber;
    }

    public void setAllelFrequency(double allelFrequency) {
        this.allelFrequency = allelFrequency;
    }

    public void setHomozygoteCount(int homozygoteCount) {
        this.homozygoteCount = homozygoteCount;
    }

    public void setOriginalNucleotide(String originalNucleotide) {
        this.originalNucleotide = originalNucleotide;
    }

    public void setVariantNucleotide(String variantNucleotide) {
        this.variantNucleotide = variantNucleotide;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    public void setEnsg(String ensg) {
        this.ensg = ensg;
    }

    public void setEnst(String enst) {
        this.enst = enst;
    }

    public void setEnsp(String ensp) {
        this.ensp = ensp;
    }

    public void setOriginalAminoAcid(String originalAminoAcid) {
        this.originalAminoAcid = originalAminoAcid;
    }

    public void setVariantAminoAcid(String variantAminoAcid) {
        this.variantAminoAcid = variantAminoAcid;
    }

    public void setIsoformPosition(int isoformPosition) {
        this.isoformPosition = isoformPosition;
    }

    public void setUniprotAccession(String uniprotAccession) {
        this.uniprotAccession = uniprotAccession;
    }

    public String getSource() {
        return source;
    }

    public String getChromosome() {
        return chromosome;
    }

    public String getGnomadAccession() {
        return gnomadAccession;
    }

    public int getChromosomePosition() {
        return chromosomePosition;
    }

    public String getDbsnpId(String dbsnpId) {
        return dbsnpId;
    }

    public int getAlleleCount() {
        return alleleCount;
    }

    public int getAllelNumber() {
        return allelNumber;
    }

    public double getAllelFrequency() {
        return allelFrequency;
    }

    public int getHomozygoteCount() {
        return homozygoteCount;
    }

    public String getOriginalNucleotide() {
        return originalNucleotide;
    }

    public String getVariantNucleotide() {
        return variantNucleotide;
    }

    public String getVariantType() {
        return variantType;
    }

    public String getGeneName() {
        return geneName;
    }

    public String getEnsg(){
        return ensg;
    }

    public String getEnst() {
        return enst;
    }

    public String getEnsp() {
        return ensp;
    }

    public String getOriginalAminoAcid() {
        return originalAminoAcid;
    }

    public String getVariantAminoAcid() {
        return variantAminoAcid;
    }

    public int getIsoformPosition() {
        return isoformPosition;
    }

    public String setUniprotAccession() {
        return uniprotAccession;
    }



}
