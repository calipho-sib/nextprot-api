package org.nextprot.api.tasks.solr;

import org.apache.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @ Generic Solr indexer for the three cores (Annotation, Publications and Terminology
 */
public abstract class GenerateSolrIndex {
    // To disable the cache temporarily: comment-out the cahemanager variable and references, and remove 'cache' from the "spring.profiles.active" properties
	private CacheManager cacheManager = null;
	private ClassPathXmlApplicationContext ctx = null;
	protected Logger logger = Logger.getLogger(GenerateSolrIndex.class);

	protected void launch(String[] args) {
		try {

			System.setProperty("spring.profiles.active", "dev, cache");
			ctx = new ClassPathXmlApplicationContext(
					"classpath:spring/commons-context.xml",
					"classpath:spring/core-context.xml");
			cacheManager = ctx.getBean(CacheManager.class);

			start(args);

		} finally {
			shutdown();
		}
	}

	public abstract void start(String[] args);
	
	protected void shutdown() {
		if(cacheManager != null){
			((EhCacheCacheManager) cacheManager).getCacheManager().shutdown();
		}
	}
	
	protected <T> T getBean(Class<T> requiredType) {
		return ctx.getBean(requiredType);
	}

}
