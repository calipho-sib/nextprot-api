package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.domain.Isoform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Repository
public class IsoformDAOImpl implements IsoformDAO {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<Isoform> findIsoformsByEntryName(String entryName) {

		String sql = sqlDictionary.getSQLQuery("isoforms-by-entry-name");

		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		List<Isoform> isoforms = null;
		isoforms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new IsoformRowMapper());
		
		if(isoforms.isEmpty()){
			//If nothing is found, remove the condition for the synonym type
			isoforms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql.replace("and syn.cv_type_id = 1 ", ""), namedParameters, new IsoformRowMapper());
		}
		
		return isoforms;

	}

	private static class IsoformMinimumRowMapper implements ParameterizedRowMapper<Isoform> {
		
		@Override
		public Isoform mapRow(ResultSet resultSet, int row) throws SQLException {
			Isoform isoform = new Isoform();
			isoform.setIsoformAccession(resultSet.getString("accession"));
			isoform.setMd5(resultSet.getString("md5"));
			isoform.setSequence(resultSet.getString("bio_sequence"));
			return isoform;
		}
	}
	
	private static class IsoformRowMapper implements ParameterizedRowMapper<Isoform> {
		
		/*
		 * Use this query to retrieve all the isoforms without a main "name" synonym:
		 * 
		 * 
			select iso.unique_name
			from sequence_identifiers iso
			where iso.cv_type_id=2 and iso.cv_status_id=1
			EXCEPT
			select iso.unique_name
			from sequence_identifiers iso
			left outer join nextprot.identifier_synonyms syn on (iso.identifier_id = syn.identifier_id) 
			left outer join nextprot.cv_synonym_types syn_types on (syn.cv_type_id = syn_types.cv_id) 
			left join nextprot.cv_synonym_qualifiers syn_qualifiers on (syn.cv_qualifier_id = syn_qualifiers.cv_id) 
			where iso.cv_type_id=2 and iso.cv_status_id=1
			and syn.is_main=true and syn.cv_type_id=1
		 *
		 *
		 */

		@Override
		public Isoform mapRow(ResultSet resultSet, int row) throws SQLException {
			Isoform isoform = new Isoform();
			isoform.setUniqueName(resultSet.getString("unique_name"));
			isoform.setSequence(resultSet.getString("bio_sequence"));
			isoform.setMd5(resultSet.getString("md5"));
			isoform.setSwissProtDisplayedIsoform(resultSet.getBoolean("is_swissprot_display"));
			// Set the main entity
			EntityName mainEntity = new EntityName();
			mainEntity.setQualifier(null); // always null in data
			mainEntity.setType("name");    // can be "name" or "accession code" but we want it to be "name" ! 
			mainEntity.setValue(resultSet.getString("synonym_name"));
			
			// there are > 9400 isoforms without a "name" synonym and they are ALL the only isoform of their entry
			// in this case all we have is an accession code we replace it with "Iso 1"
			if (resultSet.getString("syn_type").equals("accession code")) {
				mainEntity.setValue("Iso 1"); 
			}
			
			// some isoform names are just a number (integer value)
			// in this case we add a prefix "Iso " to it
			String value = mainEntity.getValue();
			if (value.matches("\\d+")) {
				value = "Iso " + value;
				mainEntity.setValue(value);
			}
			
			isoform.setMainEntityName(mainEntity);

			return isoform;
		}
	}

	@Override
	public List<EntityName> findIsoformsSynonymsByEntryName(String entryName) {

		String sql = sqlDictionary.getSQLQuery("isoforms-synonyms-by-entry-name");
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new EntityNameRowMapper());

	}

	private static class EntityNameRowMapper implements ParameterizedRowMapper<EntityName> {

		@Override
		public EntityName mapRow(ResultSet resultSet, int row) throws SQLException {

			EntityName entityName = new EntityName();
			entityName.setQualifier(resultSet.getString("syn_qualifier"));
			entityName.setType(resultSet.getString("syn_type"));
			entityName.setValue(resultSet.getString("synonym_name"));
			entityName.setMainEntityName(resultSet.getString("unique_name"));

			return entityName;
		}
	}

	private static class EquivalentIsoformsRowMapper implements ParameterizedRowMapper<Set<String>> {
		@Override
		public Set<String> mapRow(ResultSet resultSet, int row) throws SQLException {
			String[] isolist = resultSet.getString("isolist").split(",");
			Set<String> isoset = new TreeSet<>(Arrays.asList(isolist));
			return isoset;
		}
	}

	private static class EquivalentIsoformsAsEquivalentEntriesRowMapper implements ParameterizedRowMapper<Set<String>> {
		@Override
		public Set<String> mapRow(ResultSet resultSet, int row) throws SQLException {
			Set<String> entryset = new TreeSet<>();
			String[] isolist = resultSet.getString("isolist").split(",");
			for (int i=0;i<isolist.length;i++) {
				String entryAc = isolist[i].split("-")[0];
				entryset.add(entryAc);
			}
			return entryset;
		}
	}

	@Override
	public List<Set<String>> findSetsOfEquivalentIsoforms() {
		String sql = sqlDictionary.getSQLQuery("equivalent-isoforms");
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, new EquivalentIsoformsRowMapper());
	}

	@Override
	public List<Set<String>> findSetsOfEntriesHavingAnEquivalentIsoform() {
		String sql = sqlDictionary.getSQLQuery("equivalent-isoforms");
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, new EquivalentIsoformsAsEquivalentEntriesRowMapper());
	}

	@Override
	public List<Isoform> findListOfIsoformAcMd5Sequence() {
		String sql = sqlDictionary.getSQLQuery("all-iso-ac-md5-sequences");
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, new IsoformMinimumRowMapper());
	}

}
