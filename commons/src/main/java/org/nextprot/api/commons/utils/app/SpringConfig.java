package org.nextprot.api.commons.utils.app;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.logging.Logger;

/**
 * Spring Configuration object
 */
public class SpringConfig {

    private static final Logger LOGGER = Logger.getLogger(SpringConfig.class.getName());

    // To disable the cache remove 'cache' from the "spring.profiles.active" properties
    private CacheManager cacheManager = null;
    private ClassPathXmlApplicationContext ctx = null;

    private final String profiles;

    public SpringConfig() {

        this("dev, cache");
    }

    public SpringConfig(String profiles) {

        this.profiles = profiles;

        LOGGER.info("Spring config profiles: "+profiles);
    }

    /**
     * @return array of Spring resource locations
     */
    protected String[] getXmlConfigResourceLocations() {
        return new String[] {
                "classpath:spring/commons-context.xml",
                "classpath:spring/core-context.xml"
        };
    }

    public void startApplicationContext() {

        System.setProperty("spring.profiles.active", profiles);
        ctx = new ClassPathXmlApplicationContext(getXmlConfigResourceLocations());

        LOGGER.info("starting application context");

        if (profiles.contains("cache")) {
            cacheManager = ctx.getBean(CacheManager.class);
            LOGGER.info("cache manager startup");
        }
    }

    public void stopApplicationContext() {

        if (cacheManager != null) {
            ((EhCacheCacheManager) cacheManager).getCacheManager().shutdown();
            LOGGER.info("cache manager shutdown");
        }

        LOGGER.info("stopping application context");
    }

    public <T> T getBean(Class<T> requiredType) {
        return ctx.getBean(requiredType);
    }

    @Override
    public String toString() {
        return "SpringConfig{" +
                "profiles='" + profiles + '\'' +
                '}';
    }
}
