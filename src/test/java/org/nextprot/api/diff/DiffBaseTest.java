package org.nextprot.api.diff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;

import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.FileUtils;
import org.nextprot.api.commons.utils.RdfUtils;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration("classpath:META-INF/spring/core-context.xml")
abstract class DiffBaseTest {
	
	private static final Log Logger = LogFactory.getLog(DiffBaseTest.class);

	private static final Date TestDate = new Date();
	
	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SparqlService sparqlService;
	@Autowired private SparqlEndpoint endpoint;

	private int timeSQL;
	private int timeSPARQL;
	private int countSQL;
	private int countSPARQL;
	private String status;
	
	private void resetDefaultLogValues() {
		timeSPARQL=-1;
		timeSQL=-1;
		countSPARQL=-1;
		countSQL=-1;
		status="ERROR";
	}
	
	protected void diffCount(String qName) {		
		try {
			resetDefaultLogValues();		
			getCountForSparql(qName);
			getCountForSql(qName);
			status = (countSQL==countSPARQL ? "OK" : "DIFF");
			assertTrue(countSQL==countSPARQL);
		} finally {
			LogIt();
		}
	}
	
	private int getCountForSql(String qName) {
		long t0 = System.currentTimeMillis();
		String sql = getFileContentAsString("sql/" + qName + ".sql");
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

	private int getCountForSparql(String qName){
		long t0 = System.currentTimeMillis();
		String query = getFileContentAsString("sparql/" + qName + ".sparql");
		query= RdfUtils.RDF_PREFIXES + "\n" + query;
		QueryExecution qExec = endpoint.queryExecution(query);			
		com.hp.hpl.jena.query.ResultSet rs = qExec.execSelect();
	    QuerySolution qs = rs.next();
		Literal lit = qs.getLiteral("cnt");
		int count = lit.getInt();
		timeSPARQL = (int)(System.currentTimeMillis()-t0);
		countSPARQL = count;
		return countSPARQL;
	}
	
	private String getFileContentAsString(String path) {
		String resourcePath = "/org/nextprot/api/diff/";
		return FileUtils.readResourceAsString(resourcePath + path);
	}

	private void LogIt() {
		StringBuffer sb = new StringBuffer();
		sb.append("TestDate=\""+TestDate.toString() + "\";");
		sb.append("timeSQL="+timeSQL + ";");
		sb.append("timeSPARQL="+timeSPARQL + ";");
		sb.append("countSQL="+countSQL + ";");
		sb.append("countSPARQL="+countSPARQL + ";");
		sb.append("status="+status + ";");
		Logger.info(sb.toString());
	}
	
}
