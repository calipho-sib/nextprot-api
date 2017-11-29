package org.nextprot.api.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthBasic;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.aop.requests.RequestInfo;
import org.nextprot.api.core.aop.requests.RequestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Api(name = "Admin tasks", description = "Admin operations", group="Admin")
@ApiAuthBasic(roles={"ROLE_ADMIN"})
public class AdminController {

	private static final Log LOGGER = LogFactory.getLog(AdminController.class);

	@Autowired 
	private RequestManager clientRequestManager;
	
	@Autowired(required=false)
	private CacheManager cacheManager;


    @ResponseBody
    @RequestMapping(value = "/admin/cache/clear", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiMethod(path = "/admin/cache/clear", verb = ApiVerb.GET, description = "Clears the cache")
    public List<String> clearCache(HttpServletRequest request) {

        LOGGER.warn("Request to clear cache from " + request.getRemoteAddr());
        List<String> result = new ArrayList<>();

        try {
            if (cacheManager != null) {
                for (String cacheName : cacheManager.getCacheNames()) {
                    cacheManager.getCache(cacheName).clear();
                    result.add("cache " + cacheName + " cleared");
                }
            } else {
                result.add("no cache manager found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            result.add( e.getLocalizedMessage());

        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/caches/key/{key}/clear", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiMethod(path = "/admin/caches/key/{key}/clear", verb = ApiVerb.GET, description = "Clears all data from caches associated with the key")
    public List<String> clearEntriesFromCaches(HttpServletRequest request,
                                   @ApiPathParam(name = "key", description = "The name of the key",  allowedvalues = { "NX_P01308"})
                                   @PathVariable("key") String key) {

        LOGGER.debug("Request to clear caches from " + request.getRemoteAddr() + " for key "+ key);
        List<String> result = new ArrayList<>();

        try {
            if (cacheManager != null) {
                for (String cacheName : cacheManager.getCacheNames()) {

                    if (cacheManager.getCache(cacheName).get(key) != null) {
                        cacheManager.getCache(cacheName).evict(key);
                        result.add("data mapping key " + key + " evicted from cache " + cacheName);
                    }
                }
            } else {
                result.add("no cache manager found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            result.add(e.getLocalizedMessage());
            return result;
        }

        return result;
    }

	
	@ResponseBody
	@RequestMapping(value = "/admin/cache/github-doc/clear", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ApiMethod(path = "/admin/cache/github-doc/clear", verb = ApiVerb.GET, description = "Clears the documentation cache")
	public List<String> clearDocCache(HttpServletRequest request) {

		LOGGER.debug("Request to clear cache from " + request.getRemoteAddr());
		List<String> result = new ArrayList<String>();
		try {

			if (cacheManager != null) {

				if (cacheManager.getCacheNames() == null){
					result.add("caches not found");
					return result;
				}


				for(String cacheName : cacheManager.getCacheNames()){
					String name = cacheName.toLowerCase();
					if(name.startsWith("github-") || name.startsWith("seo-github-") ){
						cacheManager.getCache(cacheName).clear();
						result.add(cacheName + " cleared");
					}
					
				}
	
			} else {
				result.add("no cache manager found");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			result.add(e.getLocalizedMessage());
			return result;
		}

		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "ip")
	public String ip(HttpServletRequest request) {
		final String userIpAddress = request.getRemoteAddr();
		return userIpAddress;
	}

	@ResponseBody
	@RequestMapping(value = "/admin/requests/running", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ApiMethod(path = "/admin/requests/running", verb = ApiVerb.GET, description = "Retrives the running requests")
	public List<RequestInfo> requests(HttpServletRequest request) {
		return clientRequestManager.getRequests();
	}

	@ResponseBody
	@RequestMapping(value = "/admin/requests/last-added", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ApiMethod(path = "/admin/requests/last-added", verb = ApiVerb.GET, description = "Retrieves the lastest running requests")
	public  Map<String, RequestInfo> lastAdded(HttpServletRequest request) {
		return clientRequestManager.getLastAddedRequestByController();
	}
	
	@RequestMapping(value = "/admin/requests/last-finished")
	@ApiMethod(path = "/admin/requests/last-finished", verb = ApiVerb.GET, description = "Retrieves the latest finished requests", produces = {MediaType.APPLICATION_JSON_VALUE})
	public Map<String, RequestInfo>  lastFinished(HttpServletRequest request) {
		return clientRequestManager.getLastFinishedRequest();
	}
	

}
