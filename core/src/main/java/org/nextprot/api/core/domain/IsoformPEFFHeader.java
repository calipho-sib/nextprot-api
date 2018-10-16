package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IsoformPEFFHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum PEFF_KEY {

        DbUniqueId(),
        PName(),
        GName(),
        NcbiTaxId(),
        TaxName(),
        Length(),
        SV(),
        EV(),
        PE(),
        ModResPsi(),
        ModRes(),
        VariantSimple(),
        VariantComplex(),
        Processed();

        public String getName() {
            return "\\" + name();
        }
    }

    private String isoformAccession;

    private String isoformAccessionFormat;

    private String proteinNameFormat;

    private String geneNameFormat;

    private String ncbiTaxonomyIdentifierFormat;

    private String taxonomyNameFormat;

    private String sequenceLengthFormat;

    private String sequenceVersionFormat;

    private String entryVersionFormat;

    private String proteinEvidenceFormat;

    private String variantSimpleFormat;

    private String variantComplexFormat;

    private String modResPsiFormat;

    private String modResFormat;

    private String processedMoleculeFormat;

    public String getIsoformAccession() {
        return isoformAccession;
    }

    public void setIsoformAccession(String isoformAccession) {
        this.isoformAccession = isoformAccession;
    }

    public String getIsoformAccessionFormat() {
        return isoformAccessionFormat;
    }

    public void setIsoformAccessionFormat(String isoformAccessionFormat) {
        this.isoformAccessionFormat = isoformAccessionFormat;
    }

    public String getProteinNameFormat() {
        return proteinNameFormat;
    }

    public void setProteinNameFormat(String proteinNameFormat) {
        this.proteinNameFormat = proteinNameFormat;
    }

    public String getGeneNameFormat() {
        return geneNameFormat;
    }

    public void setGeneNameFormat(String geneNameFormat) {
        this.geneNameFormat = geneNameFormat;
    }

    public String getNcbiTaxonomyIdentifierFormat() {
        return ncbiTaxonomyIdentifierFormat;
    }

    public void setNcbiTaxonomyIdentifierFormat(String ncbiTaxonomyIdentifierFormat) {
        this.ncbiTaxonomyIdentifierFormat = ncbiTaxonomyIdentifierFormat;
    }

    public String getTaxonomyNameFormat() {
        return taxonomyNameFormat;
    }

    public void setTaxonomyNameFormat(String taxonomyNameFormat) {
        this.taxonomyNameFormat = taxonomyNameFormat;
    }

    public String getSequenceLengthFormat() {
        return sequenceLengthFormat;
    }

    public void setSequenceLengthFormat(String sequenceLengthFormat) {
        this.sequenceLengthFormat = sequenceLengthFormat;
    }

    public String getSequenceVersionFormat() {
        return sequenceVersionFormat;
    }

    public void setSequenceVersionFormat(String sequenceVersionFormat) {
        this.sequenceVersionFormat = sequenceVersionFormat;
    }

    public String getEntryVersionFormat() {
        return entryVersionFormat;
    }

    public void setEntryVersionFormat(String entryVersionFormat) {
        this.entryVersionFormat = entryVersionFormat;
    }

    public String getProteinEvidenceFormat() {
        return proteinEvidenceFormat;
    }

    public void setProteinEvidenceFormat(String proteinEvidenceFormat) {
        this.proteinEvidenceFormat = proteinEvidenceFormat;
    }

    public String getVariantSimpleFormat() {
        return variantSimpleFormat;
    }

    public void setVariantSimpleFormat(String variantSimpleFormat) {
        this.variantSimpleFormat = variantSimpleFormat;
    }

    public String getVariantComplexFormat() {
        return variantComplexFormat;
    }

    public void setVariantComplexFormat(String variantComplexFormat) {
        this.variantComplexFormat = variantComplexFormat;
    }

    public String getModResPsiFormat() {
        return modResPsiFormat;
    }

    public void setModResPsiFormat(String modResPsiFormat) {
        this.modResPsiFormat = modResPsiFormat;
    }

    public String getModResFormat() {
        return modResFormat;
    }

    public void setModResFormat(String modResFormat) {
        this.modResFormat = modResFormat;
    }

    public String getProcessedMoleculeFormat() {
        return processedMoleculeFormat;
    }

    public void setProcessedMoleculeFormat(String processedMoleculeFormat) {
        this.processedMoleculeFormat = processedMoleculeFormat;
    }

    public String getValue(PEFF_KEY key) {

        switch(key) {

            case DbUniqueId:
                return getIsoformAccessionFormat();
            case PName:
                return getProteinNameFormat();
            case GName:
                return getGeneNameFormat();
            case NcbiTaxId:
                return getNcbiTaxonomyIdentifierFormat();
            case TaxName:
                return getTaxonomyNameFormat();
            case Length:
                return getSequenceLengthFormat();
            case SV:
                return getSequenceVersionFormat();
            case EV:
                return getEntryVersionFormat();
            case PE:
                return getProteinEvidenceFormat();
            case ModResPsi:
                return getModResPsiFormat();
            case ModRes:
                return getModResFormat();
            case VariantSimple:
                return getVariantSimpleFormat();
            case VariantComplex:
                return getVariantComplexFormat();
            case Processed:
                return getProcessedMoleculeFormat();
            default:
                throw new IllegalArgumentException("unknown key "+key.getName());
        }
    }

    private static List<String> toKeyValueStringList(IsoformPEFFHeader pojo) {

        return Arrays.asList(
                pojo.getIsoformAccessionFormat(),       // \DbUniqueId
                pojo.getProteinNameFormat(),            // \PName
                pojo.getGeneNameFormat(),               // \GName
                pojo.getNcbiTaxonomyIdentifierFormat(), // \Ncbi
                pojo.getTaxonomyNameFormat(),           // \TaxName
                pojo.getSequenceLengthFormat(),         // \Length
                pojo.getSequenceVersionFormat(),        // \SV
                pojo.getEntryVersionFormat(),           // \EV
                pojo.getProteinEvidenceFormat(),        // \PE
                pojo.getModResPsiFormat(),              // \ModResPsi
                pojo.getModResFormat(),                 // \ModRes
                pojo.getVariantSimpleFormat(),          // \VariantSimple
                pojo.getVariantComplexFormat(),         // \VariantComplex
                pojo.getProcessedMoleculeFormat()       // \Processed
        );
    }

    public static String toString(IsoformPEFFHeader pojo) {

        List<String> keyValuePairsList = new ArrayList<>();

        keyValuePairsList.add("nxp:" + pojo.getIsoformAccession());
        keyValuePairsList.addAll(toKeyValueStringList(pojo));

        return keyValuePairsList.stream()
                .filter(kv -> !kv.isEmpty())
                .collect(Collectors.joining(" "));
    }

    public static Map<String, Object> toMap(IsoformPEFFHeader pojo) {

        Map<String, Object> map = new HashMap<>();

        toKeyValueStringList(pojo).stream()
                .filter(kvs -> !kvs.isEmpty())
                .forEach(kvs -> {
                    String[] kv = kvs.split("=");
                    map.put(kv[0], valueToObject(PEFF_KEY.valueOf(kv[0].substring(1)), kv[1]));
                });

        return map;
    }

    // ex:
    // \ModResPsi      (359|MOD:00046|O-phospho-L-serine)(1716|MOD:00047|O-phospho-L-threonine)(1778|MOD:00046|O-phospho-L-serine)
    // \VariantSimple  (1|V)(1|I)(2|N)(4|R)(12|V)(13|F)(18|R)
    // \VariantComplex (130|132|)(288|288|VA)(644|644|)(827|832|)(981|981|AP)(982|982|)(982|984|)
    // \Processed      (26|281|mature protein)(1|25|signal peptide)
    public static Object valueToObject(PEFF_KEY key, String value) {

        Map<Integer, Set<String>> map = new HashMap<>();

        Pattern field2Pattern = Pattern.compile("\\((\\d+)\\|(\\w+\\))", Pattern.DOTALL);
        Pattern field3Pattern = Pattern.compile("\\((\\d+)\\|(\\w*)\\|(\\w+)\\)", Pattern.DOTALL);

        switch (key) {
            case DbUniqueId:
            case PName:
            case GName:
            case NcbiTaxId:
            case TaxName:
            case Length:
            case SV:
            case EV:
            case PE:
                return value;
            case ModResPsi:
                addEntry(map, field3Pattern.matcher(value), 2);
                break;
            case ModRes:
                addEntry(map, field3Pattern.matcher(value), 3);
                break;
            case VariantSimple:
                addEntry(map, field2Pattern.matcher(value), 2);
                break;
            case VariantComplex:
                addEntry(map, field3Pattern.matcher(value), 3);
                break;
            case Processed:
                addEntry(map, field3Pattern.matcher(value), 3);
                break;
        }

        return map;
    }

    private static void addEntry(Map<Integer, Set<String>> map, Matcher matcher, int fieldPos) {

        while (matcher.find()) {

            int pos = Integer.valueOf(matcher.group(1));
            String field = matcher.group(fieldPos);

            if (!map.containsKey(pos)) {
                map.put(Integer.valueOf(matcher.group(1)), new HashSet<>());
            }
            map.get(pos).add((field != null) ? field : "");
        }
    }
}
