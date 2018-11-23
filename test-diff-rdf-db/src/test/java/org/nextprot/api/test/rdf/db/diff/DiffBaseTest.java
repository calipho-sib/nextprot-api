package org.nextprot.api.test.rdf.db.diff;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.dbunit.AbstractIntegrationBaseTest;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;


@ContextConfiguration("classpath:spring/commons-context.xml")
abstract class DiffBaseTest extends AbstractIntegrationBaseTest{
	
	@Autowired private SparqlDictionary sparqlDictionary;
	private static final Log Logger = LogFactory.getLog(DiffBaseTest.class);

	private static final Date TestDate = new Date();
	
	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SparqlService sparqlService;
	@Autowired private SparqlEndpoint sparqlEndpoint;
	@Autowired private SQLDictionary sqlDictionary;

	private String qName;
	private int timeSQL;
	private int timeSPARQL;
	private int countSQL;
	private int countSPARQL;
	private String status;
	
	protected void diffCount(String qName) {		
		try {
			this.qName=qName;
			resetDefaultLogValues();		
			getCountForSparql();
			getCountForSql();
			this.status = (this.countSQL==this.countSPARQL ? "OK" : "FAIL");
			assertTrue(this.countSQL==this.countSPARQL);
		} finally {
			logIt();
		}
	}
	
	private int getCountForSql() {
		long t0 = System.currentTimeMillis();
		String sql = sqlDictionary.getSQLQuery(qName);
		//SqlParameterSource namedParams = new MapSqlParameterSource("id", id);
		SqlParameterSource namedParams = null;
		List<Integer> counts =  new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParams, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet resultSet, int row) throws SQLException {
				return new Integer(resultSet.getInt("cnt"));
			}
		});
		timeSQL = (int)(System.currentTimeMillis()-t0);
		countSQL = counts.get(0);
		return countSQL;
	}

	private int getCountForSparql(){
		long t0 = System.currentTimeMillis();
		String query = sparqlDictionary.getSparqlWithPrefixes(qName);
		QueryExecution qExec = sparqlService.queryExecution(query);			
		com.hp.hpl.jena.query.ResultSet rs = qExec.execSelect();
	    QuerySolution qs = rs.next();
		Literal lit = qs.getLiteral("cnt");
		int count = lit.getInt();
		timeSPARQL = (int)(System.currentTimeMillis()-t0);
		countSPARQL = count;
		return countSPARQL;
	}
	

	private void resetDefaultLogValues() {
		timeSPARQL=-1;
		timeSQL=-1;
		countSPARQL=-1;
		countSQL=-1;
		status="ERROR";
	}

	private void logIt() {
		StringBuffer sb = new StringBuffer();
		sb.append("TestDate=\""+TestDate.toString() + "\";");
		String dbURL = "Error";
		try {
			sb.append("dbURL="+ dsLocator.getDataSource().getConnection().getMetaData().getURL() +";");
		} catch(Exception e) {
			sb.append("dbURL=(Error);");			
		}
		sb.append("sparqlURL="+ sparqlEndpoint.getUrl() +";");
		sb.append("TestClass="+this.getClass().getSimpleName()+";");
		sb.append("QueryName="+qName+";");
		sb.append("timeSQL="+timeSQL + ";");
		sb.append("timeSPARQL="+timeSPARQL + ";");
		sb.append("countSQL="+countSQL + ";");
		sb.append("countSPARQL="+countSPARQL + ";");
		sb.append("status="+status + ";");
		Logger.info(sb.toString());
	}
	
}
