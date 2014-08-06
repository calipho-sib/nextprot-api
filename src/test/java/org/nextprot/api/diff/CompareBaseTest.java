package org.nextprot.api.diff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
abstract class CompareBaseTest {
	
	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private SparqlService sparqlService;
	@Autowired private SparqlEndpoint endpoint;
	
	protected void diffCount(String qName) {
		assertTrue(getCountForSparql(qName) == getCountForSql(qName));
	}
	
	private int getCountForSql(String qName) {
		String sql = getFileContentAsString("sql/" + qName + ".sql");
		//SqlParameterSource namedParams = new MapSqlParameterSource("id", id);
		SqlParameterSource namedParams = null;
		List<Integer> counts =  new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sql, namedParams, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet resultSet, int row) throws SQLException {
				return new Integer(resultSet.getInt("cnt"));
			}
		});
		System.out.println("SQL Count=" + counts.get(0));
		return counts.get(0);
	
	}

	private int getCountForSparql(String qName){
		String query = getFileContentAsString("sparql/" + qName + ".sparql");
		query= RdfUtils.RDF_PREFIXES + "\n" + query;
		QueryExecution qExec = endpoint.queryExecution(query);			
		com.hp.hpl.jena.query.ResultSet rs = qExec.execSelect();
	    QuerySolution qs = rs.next();
		Literal lit = qs.getLiteral("cnt");
		int count = lit.getInt();
		System.out.println("SPARQL count=" + count);
		return count;
	}
	
	private String getFileContentAsString(String path) {
		String resourcePath = "/org/nextprot/api/diff/";
		return FileUtils.readResourceAsString(resourcePath + path);
	}
	
}
