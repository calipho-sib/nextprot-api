package org.nextprot.api.tasks.core.export;

import com.nextprot.api.annotation.builder.statement.StatementExporter;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * This application exports specified genes statement as tab-delimited files.
 * It requires one single argument output file path with optional tsv extension.
 *
 * Example of parameters:
 *
 * <ul>
 * <li>no options             : /tmp/allgenes</li>
 * <li>all genes with options : -p "dev, cache" -c "mutagenesis site" /tmp/allgenes</li>
 * <li>specific category      : -c "mutagenesis site" -g "brca1, scn2a" /tmp/two-genes.tsv</li>
 * </ul>
 *
 * Created by fnikitin on 09/08/16.
 */
public class StatementExportApp {

    private static final Logger LOGGER = Logger.getLogger(StatementExportApp.class);

    private MainConfig config;

    private StatementExportApp(String[] args) throws ParseException {

        this.config = new ArgumentParser(args).getConfig();
        LOGGER.info(config);
    }

    /**
     * @param args contains mandatory and optional arguments
     *  Mandatory : export-dir-path
     *  Optional  :
     *      -p profile (by default: dev, cache)
     *      -c filtered-categories (by default: variant, mutagenesis)
     *      -g genes (by default all genes statements are exported)
     */
    public static void main(String[] args) throws ParseException, FileNotFoundException {

        StatementExportApp app = new StatementExportApp(args);

        app.startApplicateContext();
        app.exportGenesStatements();
        app.stopApplicateContext();
    }

    public void exportGenesStatements() throws FileNotFoundException {

        StatementDao statementDao = config.getSpringConfig().getBean(StatementDao.class);
        MasterIdentifierService masterIdentifierService = config.getSpringConfig().getBean(MasterIdentifierService.class);

        StatementExporter exporter = new StatementExporter(statementDao, masterIdentifierService, config.getExporterConfig());

        LOGGER.info("exporting gene statements...");

        // get all genes if no genes were specified
        if (config.getSpecificGeneListToExport().isEmpty()) {

            Map<String, String> map = exporter.exportAllGeneStatementsAsTsvString();
            for (String geneName : map.keySet()) {

                exporter.exportAsTsvFile(config.getOutputDirname(), geneName, map.get(geneName));
                LOGGER.info("gene statements of " + geneName + " exported");
            }
        }
        else {
            for (String geneName : config.getSpecificGeneListToExport()) {

                exporter.exportAsTsvFile(config.getOutputDirname(), geneName, exporter.exportGeneStatementsAsTsvString(geneName));
                LOGGER.info("gene statements of " + geneName + " exported");
            }
        }
    }

    public void startApplicateContext() {
        LOGGER.info("starting spring application context...");
        config.getSpringConfig().startApplicateContext();
        LOGGER.info("spring application context started");
    }

    public void stopApplicateContext() {
        LOGGER.info("closing spring application context...");
        config.getSpringConfig().stopApplicateContext();
        LOGGER.info("spring application context closed");
    }

    static class MainConfig {

        private SpringConfig springConfig;
        private StatementExporter.Config exporterConfig;
        private List<String> specificGeneListToExport;
        private String outputDirname;

        SpringConfig getSpringConfig() {
            return springConfig;
        }

        void setSpringConfig(SpringConfig springConfig) {
            this.springConfig = springConfig;
        }

        StatementExporter.Config getExporterConfig() {
            return exporterConfig;
        }

        void setExporterConfig(StatementExporter.Config exporterConfig) {
            this.exporterConfig = exporterConfig;
        }

        List<String> getSpecificGeneListToExport() {
            return specificGeneListToExport;
        }

        void setSpecificGeneListToExport(List<String> specificGeneListToExport) {
            this.specificGeneListToExport = specificGeneListToExport;
        }

        String getOutputDirname() {
            return outputDirname;
        }

        void setOutputDirname(String outputDirname) {
            this.outputDirname = outputDirname;
        }

        @Override
        public String toString() {
            return  "Parameters\n" +
                    " - springConfig     : " + springConfig + "\n" +
                    " - exporterConfig   : " + exporterConfig + "\n" +
                    " - specificGeneListToExport : " + specificGeneListToExport + "\n" +
                    " - outputDirname    : '" + outputDirname + '\'';
        }
    }

    static class SpringConfig {

        // To disable the cache temporarily: comment-out the cachemanager variable and references, and remove 'cache' from the "spring.profiles.active" properties
        private CacheManager cacheManager = null;
        private ClassPathXmlApplicationContext ctx = null;

        private final String profiles;

        SpringConfig() {

            this("dev, cache");
        }

        SpringConfig(String profiles) {

            this.profiles = profiles;
        }

        void startApplicateContext() {

            System.setProperty("spring.profiles.active", profiles);
            ctx = new ClassPathXmlApplicationContext(
                    "classpath:spring/commons-context.xml",
                    "classpath:spring/core-context.xml");

            if (profiles.matches(".*cache.*"))
                cacheManager = ctx.getBean(CacheManager.class);
        }

        void stopApplicateContext() {

            if (cacheManager != null){
                ((EhCacheCacheManager) cacheManager).getCacheManager().shutdown();
            }
        }

        <T> T getBean(Class<T> requiredType) {
            return ctx.getBean(requiredType);
        }

        @Override
        public String toString() {
            return "SpringConfig{" +
                    "profiles='" + profiles + '\'' +
                    '}';
        }
    }
}
