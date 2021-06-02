package org.nextprot.api.core.dao.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.IsoformDAO;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.SlimIsoform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class IsoformDAOImpl implements IsoformDAO {

	protected final Logger LOGGER = Logger.getLogger(IsoformDAOImpl.class);

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<Isoform> findIsoformsByEntryName(String entryName) {

		String sql = sqlDictionary.getSQLQuery("isoforms-by-entry-name");
		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		try {
			List<Isoform> isoforms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new IsoformRowMapper());
			LOGGER.info("Isoforms found for entry " + entryName  + " " + isoforms.stream()
					.map(Object::toString)
					.collect(Collectors.joining(", ")));
			if(isoforms.isEmpty()){
				//If nothing is found, remove the condition for the synonym type
				isoforms = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql.replace("and syn.cv_type_id = 1 ", ""), namedParameters, new IsoformRowMapper());
				LOGGER.info("Isoforms found removing the condition for the synonym type for entry " + entryName  + " " + isoforms.stream()
						.map(Object::toString)
						.collect(Collectors.joining(", ")));
			}
			return isoforms;
		} catch(Exception e) {
			e.printStackTrace();
			LOGGER.error("Error finding isoforms for the given entry " + entryName);
			return new ArrayList<>();
		}
	}

	@Override
	public List<EntityName> findIsoformsSynonymsByEntryName(String entryName) {

		String sql = sqlDictionary.getSQLQuery("isoforms-synonyms-by-entry-name");
		
		SqlParameterSource namedParameters = new MapSqlParameterSource("unique_name", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new EntityNameRowMapper());

	}

	private static class EntityNameRowMapper extends SingleColumnRowMapper<EntityName> {

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
	public List<SlimIsoform> findOrderedListOfIsoformAcMd5SequenceFieldMap() {
		String sql = sqlDictionary.getSQLQuery("all-iso-ac-md5-sequences");
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, new SlimIsoformRowMapper());
	}

	private static class IsoformRowMapper extends SingleColumnRowMapper<Isoform> {

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

	private static class EquivalentIsoformsRowMapper extends SingleColumnRowMapper<Set<String>> {
		@Override
		public Set<String> mapRow(ResultSet resultSet, int row) throws SQLException {
			String[] isolist = resultSet.getString("isolist").split(",");
			Set<String> isoset = new TreeSet<>(Arrays.asList(isolist));
			return isoset;
		}
	}

	private static class EquivalentIsoformsAsEquivalentEntriesRowMapper extends SingleColumnRowMapper<Set<String>> {
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

	private static class SlimIsoformRowMapper extends SingleColumnRowMapper<SlimIsoform> {

		@Override
		public SlimIsoform mapRow(ResultSet resultSet, int row) throws SQLException {
			SlimIsoform isoform = new SlimIsoform();

			isoform.setAccession(resultSet.getString("accession"));
			isoform.setMd5(resultSet.getString("md5"));
			isoform.setSequence(resultSet.getString("bio_sequence"));

			return isoform;
		}
	}
}
