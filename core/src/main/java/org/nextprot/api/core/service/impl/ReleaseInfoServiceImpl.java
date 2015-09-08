package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.nextprot.api.core.domain.release.ReleaseContents;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Service
class ReleaseInfoServiceImpl implements ReleaseInfoService {

	@Autowired(required = false) private ServletContext servletContext;
	@Autowired private ReleaseInfoDao releaseInfoDao;
	@Autowired private Environment env;
	
	private static final Log LOGGER = LogFactory.getLog(ReleaseInfoServiceImpl.class);

	
	@Override
	@Cacheable("release-contents")
	public ReleaseContents findReleaseContents() {
		ReleaseContents ri = new ReleaseContents();
		ri.setDatabaseRelease(releaseInfoDao.findDatabaseRelease());
		ri.setApiRelease(this.getApiVersion());
		ri.setDatasources(releaseInfoDao.findReleaseInfoDataSources());
		return ri;
	}

	private String getApiVersion() {
		
		if(servletContext == null)
			return "NOT AVAILABLE";
	    try {
	    	String appServerHome = servletContext.getRealPath("/");
		    File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");
		    Manifest mf = new Manifest();
	    	mf.read(new FileInputStream(manifestFile));
		    Attributes atts = mf.getMainAttributes();

			String implVersion = atts.getValue("Implementation-Version");
			String gitCommitCount = readFile(appServerHome+"/WEB-INF/classes/", atts.getValue("gitCommitCountFile"));

			if (gitCommitCount != null) {

				Attributes.Name attName = new Attributes.Name("Git-Commit-Count");
				atts.put(attName, gitCommitCount);

				implVersion = implVersion.replace("-SNAPSHOT", " (build " + gitCommitCount+")");
			}

			atts.remove(new Attributes.Name("gitCommitCountFile"));

			return implVersion;
	    } catch (IOException e) {
	    	if(Arrays.asList(env.getActiveProfiles()).contains("pro")){
		    	LOGGER.warn("PRODUCTION ENVIRONMENT SHOULD BE A WAR WITH META INF" +  e.getMessage());
	    	}
	    	//e.printStackTrace();
	    	return "unknown";
		}
	}

	private String readFile(String dir, String filename) throws IOException {

		File file = new File(dir+filename);

		if (file.isFile()) {

			BufferedReader br = new BufferedReader(new FileReader(dir + filename));

			return br.readLine();
		}

		return null;
	}
}
