package org.nextprot.api.core.controller;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {
	private final Log Logger = LogFactory.getLog(CorsFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,  HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Logger.debug(request.getHeader("Origin")+" method:"+request.getMethod()+" > request: "+request.getParameterMap());

		response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
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

}
