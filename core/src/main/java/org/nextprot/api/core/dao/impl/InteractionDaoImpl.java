package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.InteractionDAO;
import org.nextprot.api.core.domain.Interactant;
import org.nextprot.api.core.domain.Interaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object that queries the database for interactions
 * 
 * @author dteixeira
 */
@Repository
public class InteractionDaoImpl implements InteractionDAO {

	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	// This should be updated in the database
	private static final String INTACT_URL = "http://www.ebi.ac.uk/intact/search/do/search?binary=";
	private static final String INTACT_URL_SELF = "http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=idA:MOLECULE_A%20AND%20idB:MOLECULE_B";

	@Override
	public List<Interaction> findInteractionsByEntry(String entryName) {
		String sql = sqlDictionary.getSQLQuery("interactions_by_entry");
		SqlParameterSource namedParameters = new MapSqlParameterSource("entryName", entryName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParameters, new InteractionRowMapper());
	}



	private static class InteractionRowMapper extends SingleColumnRowMapper<Interaction> {

		@Override
		public Interaction mapRow(ResultSet resultSet, int row) throws SQLException {

			Interaction interaction = new Interaction();
			interaction.setId(resultSet.getLong("interaction_id") + IdentifierOffset.BINARY_INTERACTION_ANNOTATION_OFFSET);
			interaction.setMd5(resultSet.getString("interaction_md5"));
			interaction.setQuality(resultSet.getString("interaction_quality"));

			interaction.setEvidenceId(resultSet.getLong("evidence_id") + IdentifierOffset.BINARY_INTERACTION_ANNOTATION_EVIDENCE_OFFSET);
			interaction.setEvidenceDatasource(resultSet.getString("evidence_datasource"));
			interaction.setEvidenceType(resultSet.getString("evidence_type"));
			interaction.setEvidenceQuality(resultSet.getString("evidence_quality"));
			interaction.setEvidenceXrefAC(resultSet.getString("evidence_xrefac"));
			interaction.setEvidenceXrefDB(resultSet.getString("evidence_xrefdb"));
			interaction.setEvidenceResourceId(resultSet.getLong("evidence_resource_id"));
			interaction.setEvidenceResourceType(resultSet.getString("evidence_resource_type"));

			interaction.setNumberOfExperiments(resultSet.getInt("number_of_experiments"));
			
			
			//So far we are only taking binary interactions, therefore:

			//There is always the entry itself 
			Interactant selfInteractant = new Interactant();
			selfInteractant.setAccession(resultSet.getString("unique_name"));
			selfInteractant.setXrefId(resultSet.getLong("entry_xref_id"));
			selfInteractant.setNextprot(true);
			selfInteractant.setEntryPoint(true);

			//And another entry, that may be present in nextprot or not
			Interactant otherInteractant = new Interactant();
			otherInteractant.setAccession(resultSet.getString("interactant_unique_name"));
			otherInteractant.setGenename(resultSet.getString("interactant_genename"));
			otherInteractant.setProteinName(resultSet.getString("interactant_protname"));
			otherInteractant.setXrefId(resultSet.getLong("interactant_xref_id"));
			otherInteractant.setDatabase(resultSet.getString("interactant_database"));
			otherInteractant.setNextprot(resultSet.getBoolean("is_interactant_in_nextprot"));
			if (otherInteractant.isNextprot()) {
				otherInteractant.setUrl("https://www.nextprot.org/entry/" + otherInteractant.getNextprotAccession() + "/interactions");
			} else {
				otherInteractant.setUrl(resultSet.getString("interactant_url"));
			}
			otherInteractant.setEntryPoint(false);
			
			//TODO make a unit test for this
			//Special case for the url
			if (otherInteractant.getAccession() != null) {
				// If it is not a self interaction
				interaction.setEvidenceXrefURL(INTACT_URL + resultSet.getString("evidence_xrefac"));
				interaction.setSelfInteraction(false);
			} else {
				// If it is a self interaction use a different URL
				interaction.setEvidenceXrefURL(INTACT_URL_SELF.replace("MOLECULE_A", selfInteractant.getAccession()).replace("MOLECULE_B", selfInteractant.getAccession()));
				interaction.setSelfInteraction(true);
			}

			
			//TODO make a unit test for this
			//If it is a self interaction add just the otherInteractant (which is the self)
			if(otherInteractant.getAccession() == null){
				interaction.addInteractant(selfInteractant);
			}else {
				interaction.addInteractant(selfInteractant);
				interaction.addInteractant(otherInteractant);
			}

			return interaction;
		}

	}

}
