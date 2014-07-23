package org.nextprot.api.core.dao.impl;

import static org.nextprot.api.commons.utils.SQLDictionary.getSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.Exon;
import org.nextprot.api.core.domain.GenomicMapping;
import org.nextprot.api.core.domain.IsoformMapping;
import org.nextprot.api.core.domain.TranscriptMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GeneDAOImpl implements GeneDAO {
	
	@Autowired private DataSourceServiceLocator dsLocator;
	

	@Override
	public List<ChromosomalLocation> findChromosomalLocationsByEntryName(String entryName) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("chromosomal-location-by-entry-name"), namedParameters, new ChromosomalLocationRowMapper());

	}
	
	private static class ChromosomalLocationRowMapper implements ParameterizedRowMapper<ChromosomalLocation> {

		@Override
		public ChromosomalLocation mapRow(ResultSet resultSet, int row) throws SQLException {
			ChromosomalLocation chromosomalLocation = new ChromosomalLocation();
			chromosomalLocation.setChromosome(resultSet.getString("chromosome"));
			chromosomalLocation.setAccession(resultSet.getString("accession"));
			chromosomalLocation.setBand(resultSet.getString("band"));
			chromosomalLocation.setStrand(resultSet.getInt("strand"));
			chromosomalLocation.setFirstPosition(resultSet.getInt("firstPosition"));
			chromosomalLocation.setLastPosition(resultSet.getInt("lastPosition"));
			chromosomalLocation.setDisplayName(resultSet.getString("displayName"));
			chromosomalLocation.setMasterGeneNames(resultSet.getString("masterGeneNames"));
			return chromosomalLocation;
		}
	}
	
	
	
	@Override
	public List<GenomicMapping> findGenomicMappingByEntryName(String entryName) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("genomic-mapping-by-entry-name"), namedParameters, new GenomicMappingRowMapper());

	}
	
	private static class GenomicMappingRowMapper implements ParameterizedRowMapper<GenomicMapping> {

		@Override
		public GenomicMapping mapRow(ResultSet resultSet, int row) throws SQLException {
			GenomicMapping genomicMapping = new GenomicMapping();
			genomicMapping.setGeneSeqId(resultSet.getLong("identifier_id"));
			genomicMapping.setAccession(resultSet.getString("accession"));
			genomicMapping.setDatabase(resultSet.getString("cv_name"));
			
			return genomicMapping;
		}
	}
	
	
	
	@Override
	public List<TranscriptMapping> findTranscriptsByIsoformNames(List<String> isoformNames) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("isoform_names", isoformNames);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("transcripts-by-isoform-names"), namedParameters, new TranscriptRowMapper());

	}
	
	private static class TranscriptRowMapper implements ParameterizedRowMapper<TranscriptMapping> {

		@Override
		public TranscriptMapping mapRow(ResultSet resultSet, int row) throws SQLException {
			TranscriptMapping transcript = new TranscriptMapping();
			transcript.setQuality(resultSet.getString("quality"));
			transcript.setReferenceGeneId(resultSet.getLong("gene_id"));
			transcript.setReferenceGeneUniqueName(resultSet.getString("gene_name"));
			transcript.setIsoformName(resultSet.getString("isoform"));

			transcript.setIsoformName(resultSet.getString("isoform"));
			transcript.setName(resultSet.getString("transcript"));
			transcript.setAccession(resultSet.getString("accession"));
			transcript.setDatabase(resultSet.getString("database_name"));
			transcript.setProteinId(resultSet.getString("ensemble_protein"));
			transcript.setBioSequence(resultSet.getString("bio_sequence"));

			return transcript;
		}
	}
	
	
	@Override
	public List<Exon> findExonsAlignedToTranscriptOfGene(String transcriptName, String geneName) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource("transcriptName", transcriptName);
		namedParameters.addValue("geneName", geneName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("exons-aligned-to-transcript"), namedParameters, new ExonMapper());

	}
	
	@Override
	public List<Exon> findExonsPartiallyAlignedToTranscriptOfGene(String isoName, String transcriptName, String geneName) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource("transcriptName", transcriptName);
		namedParameters.addValue("geneName", geneName);
		namedParameters.addValue("isoformName", isoName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(getSQLQuery("exons-partially-aligned-to-transcript"), namedParameters, new ExonMapper());

	}
	
	private static class ExonMapper implements ParameterizedRowMapper<Exon> {

		@Override
		public Exon mapRow(ResultSet resultSet, int row) throws SQLException {
			Exon exon = new Exon();
			exon.setGeneName(resultSet.getString("gene_name"));
			exon.setTranscriptName(resultSet.getString("transcript_name"));
			exon.setName(resultSet.getString("exon"));
			exon.setFirstPositionOnGene(resultSet.getInt("first_position"));
			exon.setLastPositionOnGene(resultSet.getInt("last_position"));
			exon.setRank(resultSet.getInt("rank"));
			return exon;
		}
	}
	
	@Override
	public List<IsoformMapping> getIsoformMappings(List<String> isoformNames){
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("isoform_names", isoformNames);
		List<Map<String,Object>> result = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(getSQLQuery("isoform-mappings"), namedParameters);
		
		Map<String, IsoformMapping> isoformMappings = new HashMap<String, IsoformMapping>();
		for(Map<String,Object> m : result){
			String isoName = ((String)m.get("isoform"));
			long geneId = ( (Long)m.get("reference_identifier_id"));

			String isoformMappingKey = isoName + geneId;
			if(!isoformMappings.containsKey(isoformMappingKey)){
				isoformMappings.put(isoformMappingKey, new IsoformMapping());
			}
			IsoformMapping isoformMapping = isoformMappings.get(isoformMappingKey);
			isoformMapping.setReferenceGeneId(geneId);
			isoformMapping.setUniqueName(isoName);
			isoformMapping.setBioSequence((String)m.get("bio_sequence"));
			isoformMapping.setReferenceGeneName((String)m.get("reference_gene"));
			isoformMapping.getPositionsOfIsoformOnReferencedGene().add(new AbstractMap.SimpleEntry<Integer,Integer>(((Integer)m.get("first_position")), ((Integer)m.get("last_position"))));
		}
		
		return new ArrayList<IsoformMapping>(isoformMappings.values());
	}

}
