package org.nextprot.api.web.service.impl;

import org.junit.Test;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.Assert.assertFalse;

public class PepXIntegrationAndValidationTest extends WebIntegrationBaseTest {

    @Autowired
    private PepXService pepXService;
    
	@Autowired
	private DataSourceServiceLocator dsLocator;

    @Test
    public void testPepXService() throws Exception {
    	
    	List<String> peptides = getPeptides();
    	for(String peptide : peptides){
    		List<Entry> entries = pepXService.findEntriesWithPeptides(peptide, true);
    		assertFalse(entries.get(0).getAnnotations().isEmpty());
    	}
    	
    }

    private List<String> getPeptides() throws Exception {
    	
    	String sqlToGetPeptides = "select bio_sequence from nextprot.bio_sequences  where cv_type_id = 5 limit 10";
		return new JdbcTemplate(dsLocator.getDataSource()).queryForList(sqlToGetPeptides, String.class);
    }

    

}