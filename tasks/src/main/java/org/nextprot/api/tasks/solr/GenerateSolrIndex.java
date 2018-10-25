package org.nextprot.api.tasks.solr;

import org.apache.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @ Generic Solr indexer for the three cores (Annotation, Publications and Terminology)
 */
@Deprecated
abstract class GenerateSolrIndex {
    // To disable the cache temporarily: comment-out the cachemanager variable and references, and remove 'cache' from the "spring.profiles.active" properties
	// The caches are located at /scratch/workspace-luna/nextprot-api/tasks/cache
	private CacheManager cacheManager = null;
	private ClassPathXmlApplicationContext ctx = null;
	protected Logger logger = Logger.getLogger(GenerateSolrIndex.class);

	protected void launch(String[] args) {
		try {

			//System.setProperty("spring.profiles.active", "dev");
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
