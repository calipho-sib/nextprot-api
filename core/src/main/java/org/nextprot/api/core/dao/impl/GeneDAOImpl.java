package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.GeneDAO;
import org.nextprot.api.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class GeneDAOImpl implements GeneDAO {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;
	

	@Override
	public List<ChromosomalLocation> findChromosomalLocationsByEntryName(String entryName) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("chromosomal-location-by-entry-name"), namedParameters, new ChromosomalLocationRowMapper());

	}

	@Override
	public List<ChromosomalLocation> findChromosomalLocationsByEntryNameOld(String entryName) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("chromosomal-location-by-entry-name-old"), namedParameters, new ChromosomalLocationRowMapper());

	}
	
	private static class ChromosomalLocationRowMapper implements ParameterizedRowMapper<ChromosomalLocation> {

		@Override
		public ChromosomalLocation mapRow(ResultSet resultSet, int row) throws SQLException {
			ChromosomalLocation chromosomalLocation = new ChromosomalLocation();
			chromosomalLocation.setChromosome(resultSet.getString("chromosome"));
			chromosomalLocation.setAccession(resultSet.getString("accession"));
			chromosomalLocation.setBand(resultSet.getString("band"));
			chromosomalLocation.setStrand(resultSet.getInt("strand"));
			chromosomalLocation.setBestGeneLocation(resultSet.getBoolean("best_location"));
			chromosomalLocation.setFirstPosition(resultSet.getInt("firstPosition"));
			chromosomalLocation.setLastPosition(resultSet.getInt("lastPosition"));
			chromosomalLocation.setDisplayName(resultSet.getString("displayName"));
			chromosomalLocation.setMasterGeneNames(resultSet.getString("masterGeneNames"));
			// fields for new version 
			if (resultSet.getMetaData().getColumnCount()>9) {
				chromosomalLocation.setGeneGeneNames(resultSet.getString("geneGeneNames"));
				chromosomalLocation.setMappingQuality(resultSet.getString("quality"));
			}
			return chromosomalLocation;
		}
	}
	
	@Override
	public List<GenomicMapping> findGenomicMappingByEntryName(String entryName) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("genomic-mapping-by-entry-name"), namedParameters, new GenomicMappingRowMapper());

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
	public Map<String, List<TranscriptGeneMapping>> findTranscriptMappingsByIsoformName(Collection<String> isoformNames) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("isoform_names", isoformNames);
		List<TranscriptGeneMapping> list = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("transcripts-by-isoform-names"), namedParameters, new TranscriptRowMapper());

		return list.stream().collect(Collectors.groupingBy(TranscriptGeneMapping::getIsoformName, Collectors.toList()));
	}
	
	private static class TranscriptRowMapper implements ParameterizedRowMapper<TranscriptGeneMapping> {

		@Override
		public TranscriptGeneMapping mapRow(ResultSet resultSet, int row) throws SQLException {
			TranscriptGeneMapping transcript = new TranscriptGeneMapping();
			transcript.setQuality(resultSet.getString("quality"));
			transcript.setReferenceGeneId(resultSet.getLong("gene_id"));
			transcript.setReferenceGeneUniqueName(resultSet.getString("gene_name"));
			transcript.setIsoformName(resultSet.getString("isoform"));
			transcript.setName(resultSet.getString("transcript"));
			transcript.setDatabaseAccession(resultSet.getString("accession"));
			transcript.setDatabase(resultSet.getString("database_name"));
			transcript.setProteinId(resultSet.getString("ensemble_protein"));
			transcript.setNucleotideSequenceLength(resultSet.getString("bio_sequence").length());

			return transcript;
		}
	}

	@Override
	public List<GenericExon> findExonsAlignedToTranscriptOfGene(String transcriptName, String geneName) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource("transcriptName", transcriptName);
		namedParameters.addValue("geneName", geneName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("exons-aligned-to-transcript"), namedParameters, new ExonMapper());

	}
	
	@Override
	public List<GenericExon> findExonsPartiallyAlignedToTranscriptOfGene(String isoName, String transcriptName, String geneName) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource("transcriptName", transcriptName);
		namedParameters.addValue("geneName", geneName);
		namedParameters.addValue("isoformName", isoName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("exons-partially-aligned-to-transcript"), namedParameters, new ExonMapper());

	}
	
	private static class ExonMapper implements ParameterizedRowMapper<GenericExon> {

		@Override
		public GenericExon mapRow(ResultSet resultSet, int row) throws SQLException {
			GenericExon exon = new GenericExon();

			GeneRegion geneRegion = new GeneRegion(resultSet.getString("gene_name"),
					resultSet.getInt("first_position"),
					resultSet.getInt("last_position"));

			exon.setTranscriptName(resultSet.getString("transcript_name"));
			exon.setNameDeduceAccession(resultSet.getString("exon"));
			exon.setRank(resultSet.getInt("rank")+1);
			exon.setGeneRegion(geneRegion);

			return exon;
		}
	}

	@Override
	public Map<String, List<IsoformGeneMapping>> getIsoformMappingsByIsoformName(Collection<String> isoformNames) {

		SqlParameterSource namedParameters = new MapSqlParameterSource("isoform_names", isoformNames);
		List<Map<String,Object>> result = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).queryForList(sqlDictionary.getSQLQuery("isoform-mappings"), namedParameters);

		Map<String, IsoformGeneMapping> isoformMappings = new HashMap<>();
		for(Map<String,Object> m : result){
			String isoName = ((String)m.get("isoform"));
			long geneId = ( (Long)m.get("reference_identifier_id"));

			String isoformMappingKey = isoName + "."+ geneId;
			if(!isoformMappings.containsKey(isoformMappingKey)){
				isoformMappings.put(isoformMappingKey, new IsoformGeneMapping());
			}
			IsoformGeneMapping isoformGeneMapping = isoformMappings.get(isoformMappingKey);
			isoformGeneMapping.setReferenceGeneId(geneId);
			isoformGeneMapping.setIsoformName(isoName);
			isoformGeneMapping.setReferenceGeneName((String)m.get("reference_gene"));

			GeneRegion geneRegion = new GeneRegion(isoformGeneMapping.getReferenceGeneName(),
					((Integer)m.get("first_position")),
					((Integer)m.get("last_position")));

			isoformGeneMapping.getIsoformGeneRegionMappings().add(geneRegion);
		}

		return isoformMappings.values().stream()
				.collect(Collectors.groupingBy(IsoformGeneMapping::getIsoformName, Collectors.toList()));
	}
}
