package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.DbXrefDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.DbXrefProperty;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class DbXrefDaoImpl implements DbXrefDao {

    @Autowired private SQLDictionary sqlDictionary;

	@Autowired
	private DataSourceServiceLocator dsLocator;

	@Override
	public List<PublicationDbXref> findDbXRefsByPublicationId(Long publicationId) {
		Map<String, Object> params = new HashMap<>();
		params.put("publicationId", publicationId);

        return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-publication-by-id"), params, new PublicationDbXRefRowMapper());
	}

	@Override
	public List<PublicationDbXref> findDbXRefByPublicationIds(List<Long> publicationIds) {

		if(publicationIds.isEmpty()){
			return new ArrayList<>();
		}
		Map<String, Object> params = new HashMap<>();
		params.put("publicationIds", publicationIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-publication-ids"), params, new PublicationDbXRefRowMapper());
	}

	@Override
	public List<DbXref> findDbXRefByIds(List<Long> resourceIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("resourceIds", resourceIds);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-resource-ids"), params, new DbXRefRowMapper());
	}

	@Override
	public List<DbXref> findDbXrefsByMaster(String uniqueName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master"), namedParams, new DbXRefRowMapper(uniqueName));
	}


	@Override
	public List<DbXref> findDbXrefsAsAnnotByMaster(String uniqueName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", uniqueName);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-as-annot-by-master"), namedParams, new DbXRefRowMapper(uniqueName));
	}

	// helper function to split a list
	private List<List<Long>> splitList(List<Long> list) {
		List<List<Long>> result = new ArrayList<>();
		//System.out.println("AAA splitting list of size " + list.size());
		for (int i=0;i<list.size();i+=10000) {
			int maxIndex = Math.min(i+10000, list.size());
			//System.out.println("AAA creating sublist from " + i + " to " + maxIndex);
			result.add(list.subList(i, maxIndex));
		}	
		return result;
	}


	@Override
	public List<DbXrefProperty> findDbXrefsProperties(String entryName, List<Long> resourceIds) {

		List<DbXrefProperty> result = new ArrayList<>();
		// we must split the query into multiple queries otherwise we get an SQL error:
		// the number of parameters (list of resource id) cannot exceed 32767 and miss titin has now 43012 xrefs !
		List<List<Long>> paramsList = splitList(resourceIds);
		for (List<Long> l: paramsList) {

			Map<String,Object> params = new HashMap<>();

			params.put("resourceIds", l);
			List<DbXrefProperty> someProps = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-props-by-resource-ids"), params, new DbXrefPropertyRowMapper());
			result.addAll(someProps);

			params.put("entryName", entryName);
			List<DbXrefProperty> someOtherProps = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-entry-link-props-by-entry-and-resource-ids"), params, new DbXrefPropertyRowMapper());
			result.addAll(someOtherProps);
		}
		//System.out.println("AAA final size is " + result.size());
		return result;
	}


	@Override
	public List<DbXref.EnsemblInfos> findDbXrefEnsemblInfos(List<Long> xrefIds) {

		if (!xrefIds.isEmpty()) {

			Map<String, Object> params = new HashMap<>();
			params.put("xrefIds", xrefIds);
			return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("ensembl-props-by-xref-accession"), params, new EnsemblInfosRowMapper());
		}

		return new ArrayList<>();
	}

	@Override
	public List<Long> getAllDbXrefsIds() {
		// warning: doean't work: too many rows, memory error... should simply return a jdbc resultset
		Map<String, Object> params = new HashMap<>();
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-all-ids"), params, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet resultSet, int row) throws SQLException {
				return resultSet.getLong("resource_id");
			}
		});
	}

	private static class DbXRefRowMapper extends SingleColumnRowMapper<DbXref> {

		private final String entryAccessionReferer;

		public DbXRefRowMapper() {
			this("");
		}

		public DbXRefRowMapper(String entryAccessionReferer) {
			this.entryAccessionReferer = entryAccessionReferer;
		}

		@Override
		public DbXref mapRow(ResultSet resultSet, int row) throws SQLException {
			DbXref dbXRef = new DbXref();
			dbXRef.setProteinAccessionReferer(entryAccessionReferer);
			dbXRef.setDbXrefId(resultSet.getLong("resource_id"));
			String dbName = resultSet.getString("database_name");
			dbXRef.setDatabaseName(dbName);

			String acc = resultSet.getString("accession");
			// quick fix for single error on loading with fuseki: 
			// see publication ce5476453bf570846e2baf8a893e33fd with DOI:10.1074/jbc.M414549200... and PubMed:16040616
			if (acc.endsWith("\\|[sect ]\\|")) acc = acc.substring(0,acc.length()-11); 
			// quick fix fo Bgee accessions
			if (dbName.equals("Bgee")) acc = acc.replaceAll("&amp;", "&");
			dbXRef.setAccession(acc);
			
			dbXRef.setDatabaseCategory(resultSet.getString("database_category"));
			dbXRef.setUrl(resultSet.getString("database_url"));
			dbXRef.setLinkUrl(resultSet.getString("database_link"));
			return dbXRef;
		}
	}

	private static class DbXrefPropertyRowMapper extends SingleColumnRowMapper<DbXrefProperty> {

		@Override
		public DbXrefProperty mapRow(ResultSet resultSet, int row) throws SQLException {
			DbXrefProperty prop = new DbXref.DbXrefProperty();
			prop.setDbXrefId(resultSet.getLong("resource_id"));
			prop.setPropertyId(resultSet.getLong("resource_property_id"));
			prop.setName(resultSet.getString("property_name"));
			prop.setValue(resultSet.getString("property_value"));
			return prop;
		}
	}

	private static class EnsemblInfosRowMapper extends SingleColumnRowMapper<DbXref.EnsemblInfos> {

		@Override
		public DbXref.EnsemblInfos mapRow(ResultSet resultSet, int row) throws SQLException {

			long enstXrefId = resultSet.getLong("enst_xref_id");
			long enstIsoMapId = resultSet.getLong("enst_iso_map_id");
			long enstIsoMapQual = resultSet.getLong("enst_iso_map_qual");
			String  enst = resultSet.getString("enst");
			String  iso = resultSet.getString("iso");
			String  ensg = resultSet.getString("ensg");
			String  ensp = resultSet.getString("ensp");
			
			return new DbXref.EnsemblInfos(enstXrefId, enstIsoMapId, enstIsoMapQual, enst, ensg, ensp, iso);
		}
	}
	
	private static class PublicationDbXRefRowMapper extends SingleColumnRowMapper<PublicationDbXref> {

		@Override
		public PublicationDbXref mapRow(ResultSet resultSet, int row) throws SQLException {
			PublicationDbXref dbXRef = new PublicationDbXref();
			
			dbXRef.setDbXrefId(resultSet.getLong("resource_id"));
			dbXRef.setAccession(resultSet.getString("accession"));
			dbXRef.setDatabaseCategory(resultSet.getString("database_category"));
			dbXRef.setDatabaseName(resultSet.getString("database_name"));
			dbXRef.setUrl(resultSet.getString("database_url"));
			dbXRef.setLinkUrl(resultSet.getString("database_link"));
			dbXRef.setPublicationId(resultSet.getLong("publication_id"));
			return dbXRef;
		}
	}

	@Override
	public List<DbXref> findDbXrefByAccession(String accession) {
		SqlParameterSource namedParams = new MapSqlParameterSource("accession", accession);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-accession"), namedParams, new DbXRefRowMapper());
	}


	@Override
	public List<DbXref> findAllDbXrefs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DbXref> findDbXrefByResourceId(Long resourceId) {
		SqlParameterSource namedParams = new MapSqlParameterSource("resourceId", resourceId);
		//System.out.println("DbXrefDaoImpl.findDbXrefByResourceId() resourceId:" + resourceId);
		return new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-resource-id"), namedParams, new DbXRefRowMapper());
	}

	// - - - - - -
	// new stuff
	// - - - - - -

	@Override
	public Set<DbXref> findEntryAnnotationsEvidenceXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-annotation-evidences"), namedParams, new DbXRefRowMapper(entryName));
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryIdentifierXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-identifiers"), namedParams, new DbXRefRowMapper(entryName));
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryAttachedXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-entry"), namedParams, new DbXRefRowMapper(entryName));
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryInteractionXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-interactions"), namedParams, new DbXRefRowMapper(entryName));
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findEntryInteractionInteractantsXrefs(String entryName) {
		SqlParameterSource namedParams = new MapSqlParameterSource("uniqueName", entryName);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-master-via-interactions-interactants"), namedParams, new DbXRefRowMapper(entryName));
		return new HashSet<>(xrefs);
	}

	
	
	@Override
	public Set<DbXref> findPeptideXrefs(List<String> names) {
		SqlParameterSource namedParams = new MapSqlParameterSource("names", names);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-peptide-names"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

	@Override
	public Set<DbXref> findAntibodyXrefs(List<Long> ids) {
		SqlParameterSource namedParams = new MapSqlParameterSource("ids", ids);
		List<DbXref> xrefs = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("dbxref-by-antibody-ids"), namedParams, new DbXRefRowMapper());
		return new HashSet<>(xrefs);
	}

    @Override
    public Long findXrefId(String database, String accession) {

        Map<String, Object> params = new HashMap<>();
        params.put("dbName", database);
        params.put("accession", accession);

        List<DbXref> list = new NamedParameterJdbcTemplate(dsLocator.getDataSource())
                .query(sqlDictionary.getSQLQuery("dbxref-by-accession-and-dbname"), params, new DbXRefRowMapper());

        //System.out.println("xref from db=" + database + " and ac=" + accession + ": " + list.size() + " - id: " + (list.isEmpty() ? "not found" : list.get(0).getDbXrefId()));
        
        if (list.isEmpty()) {
            return null;
        }
        else if (list.size() > 1) {
            throw new NextProtException("Multiple xref ids ("+list+"): should obtain a single xref id for db "+database+ " and accession "+ accession);
        }
        return list.get(0).getDbXrefId();
    }

    
	private static class EntryBkLinkRowMapper extends SingleColumnRowMapper<String> {
		@Override
		public String mapRow(ResultSet resultSet, int row) throws SQLException {
			String entry=resultSet.getString("entry");
			String bklink=resultSet.getString("bklink");
			return entry + "|" + bklink;
		}
	}
    

	@Override
	/**
	 * Returns a map with an entry accession as the key and a backlink URL as the value
	 * Only the first backlink URL for each entry is kept
	 * The incoming result set is sorted so that for each entry we get first MEDline abstract, 
	 * then PMC abstract and finally articles in full text.
	 * This order is chosen cos the the sentence related to the GeneRif is always highlighted
	 * in the abstracts (MEDline and PMC), always in the PMC full text articles and only sometimes 
	 * in the MEDline articles for copyright reasons 
	 */
	public Map<String, String> getGeneRifBackLinks(long pubId) {
		final String sql = 
				"select pubid,entry,bklink,annotsection " + 
				"from nextprot.europepmc_backlinks " + 
				"where pubid = :pubid " + 
				"order by entry, annotsection, substring(bklink,0,34) desc";
		
		SqlParameterSource namedParams = new MapSqlParameterSource("pubid", pubId);
		NamedParameterJdbcTemplate sqlTemplate = new NamedParameterJdbcTemplate(dsLocator.getDataSource());
		List<String> list = sqlTemplate.query(sql, namedParams, new EntryBkLinkRowMapper() );
		Map<String,String> map = new HashMap<>();
		for (String pair: list) {
			String[] splitPair = pair.split("\\|");
			String entry = splitPair[0];
			String bklink= splitPair[1];
			if (!map.containsKey(entry)) map.put(entry, bklink); // store only first entry-backlink key-value
		}
		return map;
	}
}
