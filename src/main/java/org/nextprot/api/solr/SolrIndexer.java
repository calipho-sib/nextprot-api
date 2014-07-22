package org.nextprot.api.solr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SolrIndexer implements DaoObserver {
	private final Log Logger = LogFactory.getLog(SolrIndexer.class);
	private final String ID = "id";
	private final String INFO_TYPE = "info_type";
	private final String INFO = "info";
	
	private String lastId;
	private SolrInputDocument doc;
	
	@Autowired private SolrConnectionFactory connFactory;
	private SolrServer solr;
	
	@PostConstruct
	public void init() {
		this.solr = connFactory.getServer("entry");
	}
	
	public void removeData() throws SolrServerException, IOException {
		this.solr.deleteByQuery("*:*");
		this.solr.commit();
		this.solr.optimize();
	}
	
	
	public void notify(List<Map<String, Object>> rs) throws IOException {
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		// takes care of the first iteration
		if(this.lastId == null) {
			this.lastId = (String) rs.get(0).get(ID);
			this.doc = new SolrInputDocument();
			this.doc.addField(ID, this.lastId);
		}
		
		Map<String, Object> entry;
		for(Iterator<Map<String, Object>> it = rs.iterator(); it.hasNext();) {
			entry = it.next();
			String currentId = (String)entry.get(ID);

			// finish previous doc and start new one
			if(! currentId.equals(this.lastId) || ! it.hasNext()) {
//				Logger.info("doc: "+doc);
				docs.add(doc);
				this.lastId = currentId;
				this.doc = new SolrInputDocument();
				this.doc.addField(ID, currentId);
			}
			createEntry(entry, doc);
		}
		saveFile(docs);
		
		try {
			solr.add(docs);
			solr.commit();
			solr.optimize();
		} catch (SolrServerException e) {
			Logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private SolrInputDocument createEntry(Map<String, Object> entry, SolrInputDocument doc) {
		
		String infoType = (String) entry.get(INFO_TYPE);
		
		if(infoType.equals("annotations")) {
			// we don't want to split annotation field content on <pipe>
			doc.addField(infoType, (String)entry.get(INFO));
		} else {
			// we want to split content of other fields on <pipe> if any are found
			doc = addFieldDataToDoc(entry, infoType, doc);
		}
		
		return doc;
	}
	
	
	private void saveFile(Collection<SolrInputDocument> docs) throws IOException {
		File f = new File("docs.txt");
		FileWriter writer =  new FileWriter(f);

		
		StringBuilder output = new StringBuilder();
		for(SolrInputDocument doc : docs)
			output.append(doc.toString()+"\n");
		writer.append(output.toString());
		writer.close();
	}
	
	
	private SolrInputDocument addFieldDataToDoc(Map<String, Object> entry, String fName, SolrInputDocument doc) {
		String fValue = (String) entry.get(INFO);
		if(fValue != null && fValue.length() > 0) {
			String[] values = fValue.split("\\|");
			
			for(String value : values)
				if(value.length() > 0)
					doc.addField(fName, value.trim());
		}
		return doc;
	}

}
