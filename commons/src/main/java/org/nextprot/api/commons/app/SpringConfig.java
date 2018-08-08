package org.nextprot.api.commons.app;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.logging.Logger;

/**
 * Spring Configuration object
 */
public class SpringConfig {

    private static final Logger LOGGER = Logger.getLogger(SpringConfig.class.getName());

    // To disable the cache remove 'cache' from the "spring.profiles.active" properties
    private CacheManager cacheManager = null;
    private final SpringBeanProvider springBeanProvider;
    private final String profiles;

    public SpringConfig() {

        this("dev, cache");
    }

    public SpringConfig(String profiles) {

        this.profiles = profiles;
        this.springBeanProvider = new SpringBeanProvider();
    }

    public void startApplicationContext() {

        System.setProperty("spring.profiles.active", profiles);

        if (profiles.contains("cache")) {
            cacheManager = springBeanProvider.getBean(CacheManager.class);
            LOGGER.info("cache manager startup");
        }
    }

    public void stopApplicationContext() {

        if (cacheManager != null) {
            ((EhCacheCacheManager) cacheManager).getCacheManager().shutdown();
            LOGGER.info("cache manager shutdown");
        }
    }

    public <T> T getBean(Class<T> requiredType) {
        return springBeanProvider.getBean(requiredType);
    }

    public String getProfiles() {
        return profiles;
    }

    @Override
    public String toString() {
        return "SpringConfig{" +
                "profiles='" + profiles + '\'' +
                '}';
    }
}
