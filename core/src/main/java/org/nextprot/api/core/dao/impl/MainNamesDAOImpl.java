package org.nextprot.api.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.commons.utils.SQLDictionary;
import org.nextprot.api.core.dao.MainNamesDAO;
import org.nextprot.api.core.domain.MainNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;


@Repository
public class MainNamesDAOImpl implements MainNamesDAO {
	
	@Autowired private SQLDictionary sqlDictionary;

	@Autowired private DataSourceServiceLocator dsLocator;

	@Override
	public Map<String, MainNames> getMainNamesMap() {

		List<Map<String,Object>> rows = new NamedParameterJdbcTemplate(dsLocator.getDataSource()).query(sqlDictionary.getSQLQuery("isoform-names"), new RowMapper<Map<String,Object>>() {
			@Override
			public Map<String,Object> mapRow(ResultSet resultSet, int row) throws SQLException {
				
				Map<String,Object> rec = new HashMap<>();
				
				// isoform accession
				String isoformAc = resultSet.getString("unique_name");
				rec.put("isoform_ac", isoformAc);

				// derive entry accession from isoform accession
				String masterAc = isoformAc.substring(0,isoformAc.indexOf("-"));
				rec.put("master_ac", masterAc);
				
				// entry (display) name
				rec.put("master_name", resultSet.getString("master_name"));
				
				// handle isoform name
				String isoName = resultSet.getString("isoform_name");
				try { 
					Integer.parseInt(isoName); // if it is a number, prefix it with "Iso " 
					isoName = "Iso " + isoName;
				} catch (NumberFormatException e) {}
				rec.put("isoform_name", isoName);
				
				// handle gene list
				String genes = resultSet.getString("gene_name");
				String[] geneArray = genes.split(";");
				List<String> geneList = new ArrayList<>();
				for (String g: geneArray) geneList.add(g.trim());
				rec.put("geneList", geneList);
				
				return rec;
			}
		});
		
		// now turn the list into a map
		Map<String, MainNames> result = new HashMap<>();
		
		for (Map<String,Object> rec: rows) {

			String masterAc = (String)rec.get("master_ac");

			// add a map entry for the master if not already done from a previous row
			if (! result.containsKey(masterAc)) {
				MainNames names = new MainNames();
				names.setAccession(masterAc);
				names.setName((String)rec.get("master_name"));
				names.setGeneNameList((List<String>)rec.get("geneList"));
				result.put(masterAc, names);
			}
			// add a map entry for the isoform
			MainNames names = new MainNames();
			String isoAc = (String)rec.get("isoform_ac");
			names.setAccession(isoAc);
			names.setName((String)rec.get("isoform_name"));
			names.setGeneNameList((List<String>)rec.get("geneList"));
			result.put(isoAc, names);
		}

		return result;
	}

}
