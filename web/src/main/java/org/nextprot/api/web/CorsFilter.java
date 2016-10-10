package org.nextprot.api.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {
	private final Log Logger = LogFactory.getLog(CorsFilter.class);
	
	/*
	@Override
	protected void doFilterInternalOld(HttpServletRequest request,  HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Logger.debug(request.getHeader("Origin")+" method:"+request.getMethod()+" > request: "+request.getParameterMap());

		response.addHeader("Access-Control-Allow-Origin", "*");
		if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type, Accept, Authorization");
            response.addHeader("Access-Control-Max-Age", "1800");//30 min
        }
            
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
        	response.setStatus(204);
        	response.flushBuffer();
        	return;
        }
        chain.doFilter(request, response);
        Logger.debug(request.getHeader("Origin")+" method:"+request.getMethod()+" end of request");
        
	}
	 */
	
    public CorsFilter() { 
    	logger.debug("AAA CorsFilter initialized");
    }
	
	
	private void logRequest(HttpServletRequest request) {
		Enumeration hnames = request.getHeaderNames();
		logger.debug("AAA Request        : " + request.getMethod() + " " + request.getRequestURL() + " " + request.getQueryString());
		while (hnames.hasMoreElements()) {
			String hname = (String)hnames.nextElement();
			String value = request.getHeader(hname);
		Logger.debug("AAA Request header : " + hname + "=" + value);
		}
	}
	
	private void logRequestMsg(HttpServletRequest request, String msg) {
		logger.debug("AAA Request        : " + request.getMethod() + " " + request.getRequestURL() + " " + request.getQueryString() + " - " + msg) ;
	}
	
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws ServletException, IOException {

        logRequest(req);

        String origin = req.getHeader("Origin");
        
        boolean options = "OPTIONS".equals(req.getMethod());
        if (options) {
        	logRequestMsg(req, "step1");
    		if (origin == null) return;
        	logRequestMsg(req, "step2");
            resp.addHeader("Access-Control-Allow-Headers", "origin, authorization, accept, content-type, x-requested-with");
            resp.addHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
            resp.addHeader("Access-Control-Max-Age", "3600");
        }

        resp.addHeader("Access-Control-Allow-Origin", origin == null ? "*" : origin);
        resp.addHeader("Access-Control-Allow-Credentials", "true");
    	logRequestMsg(req, "step3");
        if (!options) chain.doFilter(req, resp);
    }

}
