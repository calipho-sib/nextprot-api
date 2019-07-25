package org.nextprot.api.core.dao.impl;

import apple.laf.JRSUIConstants;
import com.hp.hpl.jena.sparql.core.Var;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.VariantFrequencyDao;
import org.nextprot.api.core.domain.VariantFrequency;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VariantFrequencyDaoImpl implements VariantFrequencyDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SQLDictionary sqlDictionary;

    @Autowired
    private DataSourceServiceLocator dataSourceLocator;

    /**
     * Return the variant frequency given the RSID
     * @param dbSNPId
     * @return
     */
    @Override
    public List<VariantFrequency> findVariantFrequency(String dbSNPId, AnnotationVariant variant) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dbsnp_id", dbSNPId);
        parameters.put("ori_aa", variant.getOriginal());
        parameters.put("var_aa", variant.getVariant());
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameters);

        List<Map<String, Object>> rows= new NamedParameterJdbcTemplate(dataSourceLocator.getStatementsDataSource())
                .queryForList(sqlDictionary.getSQLQuery(
                        "variant-frequency-by-dbSNP"),
                        parameterSource);
        List<VariantFrequency> variantFrequencies = new ArrayList<>();
        for( Map row: rows) {
            VariantFrequency variantFrequency = buildVariantFrequency(row);
            variantFrequencies.add(variantFrequency);
        }
        return variantFrequencies;
    }

    @Override
    public Map<String, List<VariantFrequency>> findVariantFrequency(Set<String> dbSNPIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("dbsnp_ids", dbSNPIds);

        // Queries for variants with dbsnp ids
        List<Map<String, Object>> rows = new NamedParameterJdbcTemplate(dataSourceLocator.getStatementsDataSource())
                .queryForList(sqlDictionary.getSQLQuery("variant-frequencies-by-dbSNP"), parameters);
        if(rows != null) {
            return buildVariantFrequencies(rows);
        } else {
            return null;
        }
    }

    private Map<String, List<VariantFrequency>> buildVariantFrequencies(List<Map<String, Object>> rows) {
        Map<String, List<VariantFrequency>> variantFrequencies = new TreeMap<>();
        for( Map row: rows) {
            VariantFrequency variantFrequency = buildVariantFrequency(row);
            if(variantFrequency != null) {
                String dbSNPId = row.get("dbsnp_id").toString();
                if(variantFrequencies.get(dbSNPId) == null) {
                    List<VariantFrequency> variantList = new ArrayList<>();
                    variantList.add(variantFrequency);
                    variantFrequencies.put(dbSNPId, variantList);
                } else {
                    List<VariantFrequency> variantList = variantFrequencies.get(dbSNPId);
                    variantList.add(variantFrequency);
                }
            }
        }
        return variantFrequencies;
    }

    private VariantFrequency buildVariantFrequency(Map row) {
        VariantFrequency variantFrequency = new VariantFrequency();
        String dbSNPId = row.get("dbsnp_id").toString();
        variantFrequency.setDbsnpId(dbSNPId);
        variantFrequency.setSource("gnomeAD");
        variantFrequency.setChromosome(row.get("chr").toString());
        variantFrequency.setChromosomePosition(Integer.parseInt(row.get("chrpos").toString()));
        variantFrequency.setOriginalNucleotide(row.get("ori_nuc").toString());
        variantFrequency.setVariantNucleotide(row.get("var_nuc").toString());
        variantFrequency.setAllelFrequency(Double.parseDouble(row.get("allele_freq").toString()));
        variantFrequency.setAllelNumber(Integer.parseInt(row.get("allele_number").toString()));
        variantFrequency.setAlleleCount(Integer.parseInt(row.get("allele_count").toString()));
        variantFrequency.setHomozygoteCount(Integer.parseInt(row.get("hom_count").toString()));
        variantFrequency.setVariantType(row.get("var_type").toString());
        variantFrequency.setGeneName(row.get("gene_name").toString());
        variantFrequency.setEnsg(row.get("ensg").toString());
        variantFrequency.setEnst(row.get("enst").toString());
        variantFrequency.setEnsp(row.get("ensp").toString());
        variantFrequency.setIsoformPosition(Integer.parseInt(row.get("iso_pos").toString()));
        variantFrequency.setOriginalAminoAcid(row.get("ori_aa").toString());
        variantFrequency.setVariantAminoAcid(row.get("var_aa").toString());
        if(row.get("uniprot_ac") != null) {
            variantFrequency.setUniprotAccession(row.get("uniprot_ac").toString());
        }
        return variantFrequency;
    }
}
