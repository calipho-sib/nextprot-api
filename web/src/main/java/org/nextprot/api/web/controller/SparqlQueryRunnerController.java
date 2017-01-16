package org.nextprot.api.web.controller;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.utils.UserQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Controller used to run automatically collections of sparql queries
 * 
 * @author pam
 */
@Lazy
@Controller
public class SparqlQueryRunnerController {

	@Autowired private SparqlDictionary sparqlDictionary;
	@Autowired private UserQueryService userQueryService;
	@Autowired private SparqlEndpoint sparqlEndpoint;

	private final String CRLF = StringUtils.CR_LF;
	private final String TAB = "\t";
	private final String COL_ID = "id";
	private final String COL_DURATION = "duration[s]";
	private final String COL_ROWS = "rows returned";
	private final String COL_STATUS = "status";
	private final String COL_ERRMSG = "error msg";
	private final String COL_TITLE = "title";
	private final String COL_TAGS = "tags";
	
	private final List<String> COLUMNS = Arrays.asList(COL_ID, COL_DURATION, COL_ROWS, COL_STATUS, COL_ERRMSG, COL_TITLE, COL_TAGS);
	
    @RequestMapping("/run/query/tags/{tags}")
    public void exportList(HttpServletResponse response, HttpServletRequest request, @PathVariable("tags") String tags) {

    	//String fileName = "toto.tsv";
        //response.setContentType("text/tab-separated-values");
        //response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    	
		long t0 = System.currentTimeMillis();

		List<String> expectedTagList = Arrays.asList(tags.split(","));
        List<UserQuery> queryList = UserQueryUtils.filterByTagList(userQueryService.getNxqQueries(), expectedTagList);
        try {
        	// write header
        	PrintWriter w = response.getWriter();
        	w.write("SPARQL end point" + TAB + sparqlEndpoint.getUrl() + CRLF);
        	w.write("Running queries with tags\t" + tags  + CRLF);
        	w.write("Query count\t"  + queryList.size() + CRLF);
    		w.write(CRLF);
        	w.write("Results" + CRLF);
        	w.flush();
        	for (String col: COLUMNS) w.write(col + TAB); 
        	w.write(CRLF);
        	// write output of each query
        	int errorCount=0;
        	int queryNum=0;
        	for (UserQuery q: queryList) {
        		queryNum++;
            	Map<String,String> result = getQueryResult(q);
            	if (result.get(COL_STATUS).equals("error")) errorCount++;
            	for (String col: COLUMNS) w.write(result.get(col) + TAB); 
            	w.write(CRLF);
            	w.flush();
            	System.out.println(new Date() + " - query " + String.valueOf(queryNum) + "/" + String.valueOf(queryList.size()) + " - " + 
            	  result.get(COL_ID)  + " - " + result.get(COL_STATUS) + " - " + result.get(COL_ROWS));
            }
    		long t = (System.currentTimeMillis()-t0)/1000;
    		w.write(CRLF);
    		w.write("Total duration[s]" + TAB + String.valueOf(t) + CRLF);
    		w.write("Total error(s)" + TAB + String.valueOf(errorCount) + CRLF);
    		w.write("Status" + TAB + (errorCount==0 ? "OK" : "ERROR(S)") + CRLF);
        	w.write("End" + CRLF);        	
        	w.flush();
        } catch (Exception e) {
            throw new NextProtException(e.getMessage(), e);
        }
    }

    private Map<String,String> getQueryResult(UserQuery q) {

		long t0 = System.currentTimeMillis();

		Map<String,String> result = new HashMap<>();
    	result.put(COL_ID, q.getPublicId());
    	result.put(COL_TITLE, q.getTitle());
    	StringBuffer tags = new StringBuffer();
    	for (String t : q.getTags()) tags.append(t + ",");
    	result.put(COL_TAGS, tags.toString());
    	result.put(COL_ROWS, "undef");
    	result.put(COL_STATUS, "OK");
    	result.put(COL_ERRMSG, "-");

		int resultsCount = 0;
		QueryExecution qExec = null;
		try {
			String sparqlQuery=getSparqlPrefixes() + CRLF + q.getSparql();
			qExec = QueryExecutionFactory.sparqlService(sparqlEndpoint.getUrl(), sparqlQuery);
			qExec.setTimeout(30 * 60 * 1000); //30 min
			ResultSet rs = qExec.execSelect();
    		while (rs.hasNext()) {
    			rs.nextBinding();
    			resultsCount++;
    		}
    		result.put(COL_ROWS, String.valueOf(resultsCount));
		} catch (Exception e) {
			result.put(COL_ERRMSG,e.getMessage().replace('\n', ' '));
			result.put(COL_STATUS, "error");
    	} finally {
    		long t = (System.currentTimeMillis()-t0)/1000;
    		result.put(COL_DURATION, String.valueOf(t));
    		if (qExec != null) qExec.close();
    	}

    	return result;
    }
	
	private String getSparqlPrefixes() {
		StringBuffer sb = new StringBuffer();
		for (String p: sparqlDictionary.getSparqlPrefixesList()) {
			sb.append(p).append("\n");
		}
		return sb.toString();
	}

}
