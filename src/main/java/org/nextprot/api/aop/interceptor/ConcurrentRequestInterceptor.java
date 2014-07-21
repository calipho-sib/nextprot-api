package org.nextprot.api.aop.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.aop.requests.RequestInfo;
import org.nextprot.api.aop.requests.RequestManager;
import org.nextprot.api.exceptions.ConcurrentRequestsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ConcurrentRequestInterceptor implements HandlerInterceptor {

	private static final Log LOGGER = LogFactory.getLog(ConcurrentRequestInterceptor.class);

	//TODO should put something like this @Value("#{users.requests.max}")
	private static final int MAX_CONCURRENT_REQUESTS_NUMBER_PER_USER = 3;
	
	private static final ConcurrentRequestsException TOO_MANY_CONCURRENT_REQUESTS_EXCEPTION = new ConcurrentRequestsException("You have reached the max number of concurrent requests.");


	@Autowired
	private RequestManager clientRequestManager;
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		//may raise an exception if too many
		checkNumberOfConcurrentRequest(request);
		return true;
	}
	
	
	/**
	 * Checks the number of concurrent requests
	 * @param requestInfo
	 */
	private void checkNumberOfConcurrentRequest(HttpServletRequest request) {

		Object remoteAddr = request.getRemoteAddr();
		if (remoteAddr == null) {
			LOGGER.warn("Can't determine remoteAddr from request");
		} else {
			String addr = remoteAddr.toString();
			int cnt = 0;
			for (RequestInfo r : clientRequestManager.getRequests()) {
				String radr = r.getRemoteAddr();
				if (radr != null && radr.equalsIgnoreCase(addr)) {
					cnt++;
					if (cnt > MAX_CONCURRENT_REQUESTS_NUMBER_PER_USER) {
						throw TOO_MANY_CONCURRENT_REQUESTS_EXCEPTION;
					}
				}
			}
		}
	}


}
