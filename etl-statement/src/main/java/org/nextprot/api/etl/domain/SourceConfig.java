package org.nextprot.api.etl.domain;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.exception.NextProtException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceConfig {

	static class NPSource {
		private String source, releaseDate, url, entryType;
		private String[] entries;

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getReleaseDate() {
			return releaseDate;
		}

		public void setReleaseDate(String releaseDate) {
			this.releaseDate = releaseDate;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getEntryType() {
			return entryType;
		}

		public void setEntryType(String entryType) {
			this.entryType = entryType;
		}

		public String[] getEntries() {
			return entries;
		}

		public void setEntries(String[] entries) {
			this.entries = entries;
		}
	}
	
	Map<String, NPSource> sources = new HashMap<>();

	public SourceConfig() {

		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("etl-config.json").getFile());
			ObjectMapper om = new ObjectMapper();
			List <NPSource> arraySources = Arrays.asList(om.readValue(file, NPSource[].class));
			
			for(NPSource nps : arraySources){
				sources.put(nps.source.toLowerCase(), nps);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Failed to parse ETL configuration", e);
		}
		
	}
	
	public NPSource getSource(String sourceName){
		return sources.get(sourceName.toLowerCase());
	}

}
