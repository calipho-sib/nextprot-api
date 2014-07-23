package org.nextprot.api.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.aop.requests.RequestInfo;
import org.nextprot.api.core.aop.requests.RequestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Lazy
@RequestMapping(value = "admin/")
//@PreAuthorize("hasRole('ROLE_ADMIN') ")
// and #request.getRemoteAddr() == '127.0.0.1'
public class AdminController {

	private static final Log LOGGER = LogFactory.getLog(AdminController.class);

	@Autowired private RequestManager clientRequestManager;
	private CacheManager cacheManager;
		
	@ResponseBody
	@RequestMapping(value = "cache/clear")
	public String clearCache(HttpServletRequest request) {

		LOGGER.warn("Request to clear cache from " + request.getRemoteAddr());
		StringBuilder sb = null;
		try {

			sb = new StringBuilder();

			if (cacheManager != null) {
				for (String cacheName : cacheManager.getCacheNames()) {
					cacheManager.getCache(cacheName).clear();
					sb.append("<li>" + cacheName + " </>");
				}
			} else {
				return "no cache manager found";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return e.getLocalizedMessage();

		}

		LOGGER.info("<html>Cache cleared for: " + sb.toString() + "</html>");
		return "Cache cleared for " + sb.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "cache/{cacheName}/clear")
	public String clearCache(HttpServletRequest request, @PathVariable("cacheName") String cacheName) {

		LOGGER.warn("Request to clear cache from " + request.getRemoteAddr());
		StringBuilder sb = null;
		try {

			sb = new StringBuilder();

			if (cacheManager != null) {

				if (cacheManager.getCache(cacheName) == null)
					return "No cache named " + cacheName + " was found.";

				cacheManager.getCache(cacheName).clear();
				sb.append("<li>" + cacheName + " </>");

			} else {
				return "no cache manager found";
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return e.getLocalizedMessage();
		}

		LOGGER.info("<html>Cache cleared for: " + sb.toString() + "</html>");
		return "Cache cleared for " + sb.toString();
	}
	
	
	@ResponseBody
	@RequestMapping(value = "ip")
	public String ip(HttpServletRequest request) {
		final String userIpAddress = request.getRemoteAddr();
		return userIpAddress;
	}

	@RequestMapping(value = "requests/running")
	@PreAuthorize("hasRole('ROLE_ADMIN') ") //and #request.getRemoteAddr() == '127.0.0.1'
	public List<RequestInfo> requests(HttpServletRequest request) {
		return clientRequestManager.getRequests();
	}

	@RequestMapping(value = "requests/last/added")
	public  Map<String, RequestInfo> lastAdded(HttpServletRequest request) {
		return clientRequestManager.getLastAddedRequestByController();
	}
	
	@RequestMapping(value = "requests/last/finised", method = RequestMethod.GET)
	public Map<String, RequestInfo>  lastFinished(HttpServletRequest request) {
		return clientRequestManager.getLastFinishedRequest();
	}
	

	/*Example on how you would do with a mapper
	 * private static String convertRequestInfoToJsonString(Map<String, RequestInfo> controllersRequest){

		ObjectMapper m = JsonUtils.getObjectMapper();
		ObjectNode root = m.createObjectNode();

		List<String> controllerMethods = new ArrayList<String>(controllersRequest.keySet());
		Collections.sort(controllerMethods);
		for (String controller : controllersRequest.keySet()) {

			ArrayNode controllersArray = m.createArrayNode();
			root.put(controller, controllersArray);
			
			RequestInfo info = controllersRequest.get(controller);
			ObjectNode requestNode = m.createObjectNode();
			for (String attribute : info.keySet())
				requestNode.put(attribute, info.get(attribute));

			controllersArray.add(requestNode);
		}

		return JsonUtils.getRepresentationInString(m, root);

	}*/

}
