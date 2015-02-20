package org.nextprot.api.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.pojo.ApiDoc;
import org.jsondoc.core.pojo.ApiMethodDoc;
import org.jsondoc.core.pojo.ApiParamDoc;
import org.jsondoc.core.pojo.ApiVerb;
import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.util.JSONDocType;
import org.jsondoc.springmvc.controller.JSONDocController;
import org.jsondoc.springmvc.scanner.SpringJSONDocScanner;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.export.impl.ExportServiceImpl;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JSONDocRoleController extends JSONDocController {


	private final static Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);

	private static String version = "0.1 beta";
	private static String basePath = ""; //no need
	private static List<String> packages = Arrays.asList(new String[] { "org.nextprot.api" });
    
    private JSONDoc jsonDoc;

	public JSONDocRoleController() {
		super(version, basePath, packages);
	}

//	@Autowired
//	private Environment env;
//
//	public void setVersion(String version) {
//		this.version = version;
//	}
//
//	public void setBasePath(String basePath) {
//		this.basePath = basePath;
//	}
//
//	public void setPackages(List<String> packages) {
//		this.packages = packages;
//	}
//
	@PostConstruct
	public void init() {
		version = getMavenVersion();
		jsonDoc = new SpringJSONDocScanner().getJSONDoc(version, basePath, packages);
		for(Set<ApiDoc> apiDocs: jsonDoc.getApis().values()) {
			for(ApiDoc apiDoc: apiDocs) {
				ApiMethodDoc met = null;
				if(apiDoc.getName().equals("Entry") && apiDoc.getMethods() != null && !apiDoc.getMethods().isEmpty()) {
					met = apiDoc.getMethods().iterator().next();
				}
				
				if (apiDoc.getName().equals("Entry")) {
					for (AnnotationApiModel model: AnnotationApiModel.values()) {
						ApiMethodDoc m = new ApiMethodDoc();
						m.setQueryparameters(met.getQueryparameters());
						Set<String> produces = new HashSet<String>();
						produces.add(MediaType.APPLICATION_XML_VALUE);
						m.setProduces(produces);
						m.setConsumes(met.getConsumes());
						Set<ApiParamDoc> set = new HashSet<ApiParamDoc>();
						String[] allowedvalues = {"NX_P01308"};
						set.add(new ApiParamDoc("entry", 
								"Exports only the " + model.getApiTypeName().toLowerCase() + " from an entry. It locates on the hierarchy: " + model.getHierarchy(), 
								new JSONDocType("string"), 
								"true", 
								allowedvalues, 
								null, 
								null));
						m.setPathparameters(set);
						m.setPath("/entry/{entry}/" + StringUtils.decamelizeAndReplaceByHyphen(model.getApiTypeName()));
						m.setVerb(ApiVerb.GET);
						apiDoc.getMethods().add(m);
					}
				}
			}
		}
	}

	@RequestMapping(value = JSONDocController.JSONDOC_DEFAULT_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public @ResponseBody JSONDoc getApi() {
		Set<String> contextRoles = NPSecurityContext.getCurrentUserRoles();
		LOGGER.info("Context roles");
		for (String role : contextRoles) {
			LOGGER.info(role);
		}

		Map<String, Set<ApiDoc>> contextApis = new TreeMap<String, Set<ApiDoc>>();
		for(Entry<String, Set<ApiDoc>> api: jsonDoc.getApis().entrySet()) {

			Set<ApiDoc> contextApiDocs = new TreeSet<ApiDoc>();
			
			for (ApiDoc apiDoc : api.getValue()) {
//				boolean devMode = false;
//				if (env != null) {
//					String[] pfs = env.getActiveProfiles();
//					if (pfs != null) {
//						for (String e : pfs) {
//							if (e.equalsIgnoreCase("dev")) {
//								devMode = true;
//								break;
//							}
//						}
//					}
//				}
				if (apiDoc.getAuth() == null || apiDoc.getAuth().equals("ROLE_ANONYMOUS") || 
						(contextRoles != null && !Collections.disjoint(contextRoles, apiDoc.getAuth().getRoles()))) {
					LOGGER.info("Add " + apiDoc.getName() + "ApiDoc to the current user");
					contextApiDocs.add(apiDoc);
				}
			}
			if (!contextApiDocs.isEmpty()) {
				contextApis.put(api.getKey(), contextApiDocs);
			}
		}
		
		JSONDoc contextJSONDoc = new JSONDoc(version, basePath);
		contextJSONDoc.setApis(contextApis);
		contextJSONDoc.setObjects(jsonDoc.getObjects());
		contextJSONDoc.setFlows(jsonDoc.getFlows());

		return contextJSONDoc;
	}
	
	@Autowired
	ServletContext servletContext;
	
	private String getMavenVersion() {
	    try {

	    	String appServerHome = servletContext.getRealPath("/");
		    File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");
		    Manifest mf = new Manifest();
	    	mf.read(new FileInputStream(manifestFile));
		    Attributes atts = mf.getMainAttributes();
		    return atts.getValue("Implementation-Build");

	    } catch (IOException e) {
	    	return "unknown";
		}
	}
}
