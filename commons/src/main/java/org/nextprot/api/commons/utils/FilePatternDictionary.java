package org.nextprot.api.commons.utils;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Utility class that read resource from classpath. Queries are hold in memory
 * for improved performance
 * 
 * @author dteixeira
 */
public abstract class FilePatternDictionary {

	private static Logger log = Logger.getLogger(FilePatternDictionary.class);

	private Map<String, String> resourcesMap = null;

	protected abstract String getLocation();

	protected abstract String getExtension();

	protected synchronized Map<String, String> getResourcesMap() {
		if(resourcesMap == null){
			loadResources();
		}
		return resourcesMap;
	}

	protected String getResource(String resource) {
		if (getResourcesMap().containsKey(resource)) {
			return getResourcesMap().get(resource);
		} else {
			log.error("NO file found" + resource);
			throw new NextProtException("Resource " + resource
					+ " not found on a total of " + getResourcesMap().size()
					+ " resources");
		}
	}

	protected void loadResources() {

		resourcesMap = new TreeMap<>();

		Resource[] resources;
		try {
			
			resources = new PathMatchingResourcePatternResolver().getResources(getLocation());

			for (Resource r : resources) {
				resourcesMap.put(r.getFilename().replace(getExtension(), ""),
						Resources.toString(r.getURL(), Charsets.UTF_8));
			}

		} catch (IOException e) {
			throw new NextProtException("Error on loading SQL Dict", e);
		}
	}

}
