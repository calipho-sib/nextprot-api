package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.nextprot.api.core.dao.ReleaseStatsDao;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.release.ReleaseInfoDataSources;
import org.nextprot.api.core.domain.release.ReleaseInfoStats;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Service
class ReleaseInfoServiceImpl implements ReleaseInfoService {

	@Autowired(required = false) private ServletContext servletContext;
	@Autowired private ReleaseInfoDao releaseInfoDao;
	@Autowired private ReleaseStatsDao releaseStatsDao;
	@Autowired private Environment env;
	@Autowired private MasterIdentifierService masterIdentifierService;

	private static final Log LOGGER = LogFactory.getLog(ReleaseInfoServiceImpl.class);

	@Override
	@Cacheable("release-versions")
	public ReleaseInfoVersions findReleaseVersions() {
		ReleaseInfoVersions ri = new ReleaseInfoVersions();
		ri.setDatabaseRelease(releaseInfoDao.findDatabaseRelease());
		ri.setApiRelease(this.getApiVersion());
		return ri;
	}

	@Override
	@Cacheable("release-stats")
	public ReleaseInfoStats findReleaseStats() {

		ReleaseInfoStats rs = new ReleaseInfoStats();

		List<ReleaseStatsTag> stats = releaseStatsDao.findTagStatistics();

		for (ReleaseStatsTag statsTag : stats) {

			if ("PROTEIN_LEVEL_MASTER".equals(statsTag.getTag())) {
				statsTag.setCount(masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.PROTEIN_LEVEL).size());
			}
			else if ("TRANSCRIPT_LEVEL_MASTER".equals(statsTag.getTag())) {
				statsTag.setCount(masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.TRANSCRIPT_LEVEL).size());
			}
			else if ("HOMOLOGY_MASTER".equals(statsTag.getTag())) {
				statsTag.setCount(masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.HOMOLOGY).size());
			}
			else if ("PREDICTED_MASTER".equals(statsTag.getTag())) {
				statsTag.setCount(masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.PREDICTED).size());
			}
			else if ("UNCERTAIN_MASTER".equals(statsTag.getTag())) {
				statsTag.setCount(masterIdentifierService.findEntryAccessionsByProteinExistence(ProteinExistence.UNCERTAIN).size());
			}
		}

		rs.setTagStatistics(stats);

		return rs;
	}

	@Override
	@Cacheable("release-data-sources")
	public ReleaseInfoDataSources findReleaseDatasources() {

		ReleaseInfoDataSources sources = new ReleaseInfoDataSources();
		sources.setDatasources(releaseStatsDao.findReleaseInfoDataSources());
		return sources;
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
			String gitCommitHash = readFile(appServerHome+"/WEB-INF/classes/", atts.getValue("gitCommitHashFile"));
			String gitBranchName = readFile(appServerHome+"/WEB-INF/classes/", atts.getValue("gitBranchNameFile"));

			if (gitCommitCount != null) {

                StringBuilder sb = new StringBuilder(" (build " + gitCommitCount);

                if (gitCommitHash != null) {

                    sb.append("#").append(gitCommitHash);
                }

                if (gitBranchName != null) {

                    sb.append(" [branch ").append(gitBranchName).append("]");
	    		}

	    		sb.append(")");

                implVersion = implVersion.replace("-SNAPSHOT", sb.toString());
            }

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
			try (BufferedReader br = new BufferedReader(new FileReader(dir + filename))) {
				return br.readLine();
			}
		}
		return null;
	}
}
