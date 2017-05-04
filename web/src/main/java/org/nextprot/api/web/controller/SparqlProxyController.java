package org.nextprot.api.web.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

@Controller
public class SparqlProxyController extends ServletWrappingController implements InitializingBean{
	
    @Value("${sparql.url}")
    private String sparqlEndpoint;
	
	@Override
	public void afterPropertiesSet() throws Exception{
		this.setServletClass(org.mitre.dsmiley.httpproxy.ProxyServlet.class);
		this.setServletName("sparql-proxy");
		
		Properties props = new Properties();
		props.setProperty("targetUri", sparqlEndpoint);
		this.setInitParameters(props);
		
		super.afterPropertiesSet();
	}
	
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return super.handleRequestInternal(request, response);
	}

}
