package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.IdentifierDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.core.utils.dbxref.resolver.DbXrefURLResolverDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class IdentifierDaoImpl implements IdentifierDao {

	private static final Map<String, String> DB_TYPE_NP1_NAMES;

	static {
		DB_TYPE_NP1_NAMES = new HashMap<>();

		// See https://issues.isb-sib.ch/browse/CALIPHOMISC-359
		DB_TYPE_NP1_NAMES.put("UNIPROT", "UniProtKB");
		DB_TYPE_NP1_NAMES.put("ACCESSION_CODE", "UniProtKB");
		DB_TYPE_NP1_NAMES.put("CLONE_NAME", "CLONE NAMES");
		DB_TYPE_NP1_NAMES.put("MICROARRAY_PROBE", "MICROARRAY PROBE IDENTIFIERS");
		DB_TYPE_NP1_NAMES.put("NCBI", "NCBI");
		DB_TYPE_NP1_NAMES.put("ADDITIONAL_IDENTIFIER", "ADDITIONAL IDENTIFIERS");
		DB_TYPE_NP1_NAMES.put("ENSEMBL", "ENSEMBL");
	}

	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SQLDictionary sqlDictionary;

	@Override
	public List<Identifier> findIdentifiersByMaster(String uniqueName) {
		Map<String, Object> params = new HashMap<>();
		params.put("uniqueName", uniqueName);

		return new NamedParameterJdbcTemplate(dsLocator.getDataSource())
                .query(sqlDictionary.getSQLQuery("identifiers-by-master-unique-name"),
                        params, new IdentifierRowMapper(uniqueName));
	}
	
	private static class IdentifierRowMapper implements ParameterizedRowMapper<Identifier> {

		private String masterUniqueName;
		
		public IdentifierRowMapper(String masterUniqueName) {
			this.masterUniqueName=masterUniqueName;
		}
		
		@Override
		public Identifier mapRow(ResultSet resultSet, int row) throws SQLException {
			Identifier identifier = new Identifier();
			String name = resultSet.getString("identifier_name");
			String db = resultSet.getString("db_name");
			String linkTemplate = resultSet.getString("link_template");
			identifier.setName(name);
			identifier.setDatabase(db);
			identifier.setLink(resolveLink(this.masterUniqueName, name, db, linkTemplate));
			identifier.setType(resultSet.getString("type"));
			String typeClass = resultSet.getString("type_class");
			identifier.setDatabaseCategory(DB_TYPE_NP1_NAMES.containsKey(typeClass) ? DB_TYPE_NP1_NAMES.get(typeClass) : typeClass);

			return identifier;
		}
		

		
		private static DbXrefURLResolverDelegate resolver = null;
		/**
		 * The method received the parameters needed to resolve the link of the identifier if necessary
		 * It uses the general mechanism provided by DbXrefURLResolverDelegate
		 * The Xref is built based on the parameters passed to the method. The xref needs to be tuned in 2 cases:
		 * PIR: requires the property "entry name" to be set for its specific resolver to work properly
		 * UniProtKB: is NOT a declared database, it is replaced with the right db name (UniProt) and default template so that uses the normal pipeline
		 * @param masterUniqueName
		 * @param ac
		 * @param db
		 * @param linkTemplate
		 * @return a resolved link for the identifier as a String
		 */
		private static String resolveLink(String masterUniqueName, String ac, String db, String linkTemplate) {
			if (resolver==null) resolver = new DbXrefURLResolverDelegate();
			if (db == null) return null;
			DbXref xref = new DbXref();
			xref.setProteinAccessionReferer(masterUniqueName);
			xref.setAccession(ac);
			xref.setDatabaseName(db);
			xref.setLinkUrl(linkTemplate);
			if ("UniProtKB".equals(db) ) {
				xref.setDatabaseName("UniProt");
				xref.setLinkUrl("http://www.uniprot.org/uniprot/%u");
			} else if ("PIR".equals(db)) {
				List<DbXrefProperty> props = new ArrayList<>();
				DbXrefProperty prop = new DbXrefProperty();
				prop.setName("entry name");
				prop.setValue(ac);
				props.add(prop);
				xref.setProperties(props);
			}
			return resolver.resolve(xref);
		}
		
	}
}
