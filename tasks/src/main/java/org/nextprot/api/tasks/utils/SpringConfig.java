package org.nextprot.api.tasks.utils;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring Configuration object
 */
public class SpringConfig {

    // To disable the cache temporarily: comment-out the cachemanager variable and references, and remove 'cache' from the "spring.profiles.active" properties
    private CacheManager cacheManager = null;
    private ClassPathXmlApplicationContext ctx = null;

    private final String profiles;

    public SpringConfig() {

        this("dev, cache");
    }

    public SpringConfig(String profiles) {

        this.profiles = profiles;
    }

    public void startApplicationContext() {

        System.setProperty("spring.profiles.active", profiles);
        ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/commons-context.xml",
                "classpath:spring/core-context.xml");

        if (profiles.matches(".*cache.*"))
            cacheManager = ctx.getBean(CacheManager.class);
    }

    public void stopApplicationContext() {

        if (cacheManager != null) {
            ((EhCacheCacheManager) cacheManager).getCacheManager().shutdown();
        }
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
