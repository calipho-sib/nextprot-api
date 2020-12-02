package org.nextprot.api.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.ReleaseInfoDao;
import org.nextprot.api.core.dao.ReleaseStatsDao;
import org.nextprot.api.core.domain.GlobalEntryStatistics;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.release.ReleaseInfoDataSources;
import org.nextprot.api.core.domain.release.ReleaseInfoStats;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nextprot.api.commons.constants.AnnotationCategory.*;

@Service
class ReleaseInfoServiceImpl implements ReleaseInfoService {

	@Autowired(required = false) private ServletContext servletContext;
	@Autowired private ReleaseInfoDao releaseInfoDao;
	@Autowired private ReleaseStatsDao releaseStatsDao;
	@Autowired private Environment env;
	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private StatisticsService statisticsService;
	@Autowired private AnnotationService annotationService;
	@Autowired private OverviewService overviewService;
	

	private static final Log LOGGER = LogFactory.getLog(ReleaseInfoServiceImpl.class);
	private static final Set<String> NON_FUNCTIONAL_GO_ACC = new HashSet<>(Arrays.asList(
			// GO_MOLECULAR_FUNCTION
			"GO:0005524",	// ATP binding
			"GO:0000287",	// magnesium ion binding
			"GO:0005515",	// protein binding
			"GO:0042802",	// identical protein binding
			"GO:0008270",	// zinc ion binding
			"GO:0005509",	// calcium ion binding
			"GO:0003676",	// nucleic acid binding
			"GO:0003824",	// catalytic activity
			"GO:0046914",	// transition metal ion binding
			"GO:0046872",	// metal ion binding
			// GO_BIOLOGICAL_PROCESS
			"GO:0051260",	// protein homooligomerization
			"GO:0007165",	// signal transduction
			"GO:0035556"));	// intracellular signal transduction


	@Override
	@Cacheable(value = "release-versions", sync = true)
	public ReleaseInfoVersions findReleaseVersions() {
		ReleaseInfoVersions ri = new ReleaseInfoVersions();
		ri.setDatabaseRelease(releaseInfoDao.findDatabaseRelease());
		ri.setApiRelease(this.getApiVersion());
		return ri;
	}

	@Override
	@Cacheable(value = "release-stats", sync = true)
	public ReleaseInfoStats findReleaseStats() {

		ReleaseInfoStats rs = new ReleaseInfoStats();

		rs.setDatabaseRelease(releaseInfoDao.findDatabaseRelease());

		List<ReleaseStatsTag> stats = releaseStatsDao.findTagStatistics();

		updateTagCounts(stats);

		rs.setTagStatistics(stats);

		return rs;
	}

	private void updateTagCounts(List<ReleaseStatsTag> stats) {

		GlobalPublicationStatistics publisStats = statisticsService.getGlobalPublicationStatistics();
		GlobalEntryStatistics globalEntryStats = statisticsService.getGlobalEntryStatistics();

		for (ReleaseStatsTag statsTag : stats) {

			// Update PEs
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

			// Update publis stats
			else if ("PUBLI".equals(statsTag.getTag())) {
				statsTag.setCount(publisStats.getTotalNumberOfPublications());
			}

			else if ("CITED_PUBLI".equals(statsTag.getTag())) {
				statsTag.setCount(publisStats.getNumberOfCitedPublications());
			}

			else if ("COMPUTED_PUBLI".equals(statsTag.getTag())) {
				statsTag.setCount(publisStats.getNumberOfComputationallyMappedPublications());
			}

			else if ("LARGE_SCALE_PUBLI".equals(statsTag.getTag())) {
				statsTag.setCount(publisStats.getNumberOfLargeScalePublications());
			}

			else if ("CURATED_PUBLI".equals(statsTag.getTag())) {
				statsTag.setCount(publisStats.getNumberOfCuratedPublications());
			}

			// Update global entry stats
			else if ("INTERACTION".equals(statsTag.getTag())) {
				statsTag.setCount(globalEntryStats.getNumberOfEntriesWithBinaryInteraction());
			}

			else if ("W_EXPRESSION_MASTER".equals(statsTag.getTag())) {
				statsTag.setCount(globalEntryStats.getNumberOfEntriesWithExpressionProfile());
			}

			// Update other stats
			else if ("CVTERM".equals(statsTag.getTag())) {
				statsTag.setCount(statisticsService.getCvTermCount());
			}
		}
	}

	@Cacheable("annot-wo-function-count")
	public ReleaseStatsTag getAnnotationWithoutFunctionTag() {

		// We filter out entries with annotation of one of these categories
		final Set<AnnotationCategory> notAllowedCategories =
				Stream.of(FUNCTION_INFO, CATALYTIC_ACTIVITY, TRANSPORT_ACTIVITY, PATHWAY).collect(Collectors.toSet());

		// We filter out entries with GO MF or GO BP which are not one of notFunctionGoAcc
		final Set<AnnotationCategory> allowedCategories =
				Stream.of(GO_MOLECULAR_FUNCTION, GO_BIOLOGICAL_PROCESS).collect(Collectors.toSet());

		long count = masterIdentifierService.findUniqueNames().parallelStream()
				.filter(ac -> ! overviewService.findOverviewByEntry(ac).getProteinExistence().equals(ProteinExistence.UNCERTAIN))
				.map(ac -> annotationService.findAnnotations(ac))
				.filter(annotList -> annotList.stream().noneMatch(
						a -> notAllowedCategories.contains(a.getAPICategory())))
				.filter(annotList -> annotList.stream().noneMatch(
						a -> allowedCategories.contains(a.getAPICategory())
								&& !NON_FUNCTIONAL_GO_ACC.contains(a.getCvTermAccessionCode())))
				.count();

		ReleaseStatsTag releaseStatsTag = new ReleaseStatsTag();
		releaseStatsTag.setTag("WO_FUNCTION_MASTER");
		releaseStatsTag.setDescription("Entries without annotated function");
		releaseStatsTag.setCount((int) count);
		releaseStatsTag.setSortOrder(225);
		releaseStatsTag.setCategroy("Annotations");
		return releaseStatsTag;
	}

	@Override
	@Cacheable(value = "release-data-sources", sync = true)
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
