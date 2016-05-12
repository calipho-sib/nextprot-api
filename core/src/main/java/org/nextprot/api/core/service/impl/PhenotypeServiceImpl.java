package org.nextprot.api.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioEntry;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.phenotypes.PhenotypeAnnotation;
import org.nextprot.api.core.service.PhenotypeService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PhenotypeServiceImpl implements PhenotypeService {

	@Override
	public Map<String, List<PhenotypeAnnotation>> findPhenotypeAnnotations(String entryName) {
		Map<String, List<PhenotypeAnnotation>> result = new HashMap<>();
		JSONPhenotypeList phenotypes = getJson("brca1-phenotypes");
		for(JSONPhenotype p : phenotypes){
			if(!result.containsKey(p.subject)){
				result.put(p.subject, new ArrayList<PhenotypeAnnotation>());
			}
			
			PhenotypeAnnotation pa = new PhenotypeAnnotation();
			
			pa.setImpact(p.impact);
			pa.setEffect(p.effect);
			if(!p.category.equals("")){
				pa.setCvTermName(p.cvName);
				pa.setCategory(p.category);
			}

			if(p.bioObject != null && !p.bioObject.equals("")){
				BioEntry bioEntry = new BioEntry();
		        bioEntry.setAccession(p.bioObject);
				pa.setBioObject(bioEntry);
			}

			result.get(p.subject).add(pa);
		}
		return result;
	}
	
	private JSONPhenotypeList getJson(String entry){

		ClassLoader classLoader = this.getClass().getClassLoader();
		File file = new File(classLoader.getResource("brca1-phenotypes.json").getFile());
		
	    try {
	    	String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
			ObjectMapper mapper = new ObjectMapper();
	    	return mapper.readValue(content, JSONPhenotypeList.class);
	    } catch (IOException e) {
			throw new NextProtException("Some error while reading json response " + e.getLocalizedMessage());
		}
	}
	

	@SuppressWarnings("serial")
	private static class JSONPhenotypeList extends ArrayList<JSONPhenotype>{};

	@SuppressWarnings({"unused"})
	private static class JSONPhenotype {

		private String impact;
		private String effect;
		private String subject;
		private String cvName;
		private String category;
		private String bioObject;

		public String getImpact() {
			return impact;
		}
		public void setImpact(String impact) {
			this.impact = impact;
		}
		public String getEffect() {
			return effect;
		}
		public void setEffect(String effect) {
			this.effect = effect;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
		public String getCvName() {
			return cvName;
		}
		public void setCvName(String cvName) {
			this.cvName = cvName;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getBioObject() {
			return bioObject;
		}
		public void setBioObject(String bioObject) {
			this.bioObject = bioObject;
		}

	}



}
