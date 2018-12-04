package org.nextprot.api.core.dao.impl;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.IdentifierDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Identifier;
import org.nextprot.api.core.service.dbxref.resolver.DbXrefURLResolverDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

        List<Identifier> ids = new NamedParameterJdbcTemplate(
                dsLocator.getDataSource()).query(
                sqlDictionary.getSQLQuery("identifiers-by-master-unique-name"), params, new IdentifierRowMapper(uniqueName));

        return ids.stream()
                .filter(Objects::nonNull)
                .filter(i -> isValidIdentifier(i))
                .collect(Collectors.toList());
    }

    private boolean isValidIdentifier(Identifier identifier) {
        // See CALIPHOMISC-489
        if (! "Ensembl".equals(identifier.getDatabase())) return true;
        if (identifier.getName().startsWith("ENSG")) return true;
        if (identifier.getName().startsWith("ENSP")) return true;
        if (identifier.getName().startsWith("ENST")) return true;
        return false;
    }

	private static class IdentifierRowMapper extends SingleColumnRowMapper<Identifier> {

		private String masterUniqueName;
		
		public IdentifierRowMapper(String masterUniqueName) {
			this.masterUniqueName=masterUniqueName;
		}
		
		@Override
		public Identifier mapRow(ResultSet resultSet, int row) throws SQLException {
			Identifier identifier = new Identifier();
			String name = resultSet.getString("identifier_name");
			String db = resultSet.getString("db_name");
			String typ = resultSet.getString("type");
			identifier.setName(name);
			identifier.setDatabase(db);
			identifier.setType(typ);
			String linkTemplate = resultSet.getString("link_template");
			String link = resolveLink(this.masterUniqueName, typ, name, db, linkTemplate);
			identifier.setLink(link);
			String typeClass = resultSet.getString("type_class");
			identifier.setDatabaseCategory(DB_TYPE_NP1_NAMES.containsKey(typeClass) ? DB_TYPE_NP1_NAMES.get(typeClass) : typeClass);
			//System.out.println(name + " " + db  + " " + typ  + " " + typeClass  + " " + link);

			return identifier;
		}
		

		
		private static DbXrefURLResolverDelegate resolver = null;
		/**
		 * The method received the parameters needed to resolve the link of the identifier if necessary
		 * It uses the general mechanism provided by DbXrefURLResolverDelegate
		 * The Xref is built based on the parameters passed to the method. The xref needs to be tuned in 1 case:
		 * UniProtKB: is NOT a declared database, it is replaced with the right db name (UniProt) 
		 * and default template so that uses the normal pipeline
		 * @param masterUniqueName
		 * @param ac
		 * @param db
		 * @param linkTemplate
		 * @return a resolved link for the identifier as a String
		 */
		private static String resolveLink(String masterUniqueName, String typ, String ac, String db, String linkTemplate) {
			
			if (resolver==null) resolver = new DbXrefURLResolverDelegate();

			// case of identifiers known to have no link
			if (db == null) return null; 
			// case of PIR identifiers, the link is irrelevant (Amos dixit: specs NP2 page validation) 
			if ("PIR".equals(db)) return null;
			// case of uniprot secondary acs & entry name identifiers, link useless (Amos dixit: specs NP2 page validation) 
			if ("UniProtKB".equals(db) && ! "Primary AC".equals(typ) ) return null;

			// normal cases
			DbXref xref = new DbXref();
			xref.setProteinAccessionReferer(masterUniqueName);
			xref.setAccession(ac);
			xref.setDatabaseName(db);
			xref.setLinkUrl(linkTemplate);
			// special case for uniprot primary ac
			if ("UniProtKB".equals(db) ) {
				xref.setDatabaseName("UniProt");
				xref.setLinkUrl("http://www.uniprot.org/uniprot/%u");
			} 
			return resolver.resolve(xref);
		}
		
	}
}
