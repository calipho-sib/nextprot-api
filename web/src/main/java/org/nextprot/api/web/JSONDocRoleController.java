package org.nextprot.api.web;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.pojo.ApiDoc;
import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.util.JSONDocUtils;
import org.jsondoc.springmvc.controller.JSONDocController;
import org.nextprot.api.core.service.export.impl.ExportServiceImpl;
import org.nextprot.api.user.security.NPSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JSONDocRoleController extends JSONDocController {

	private final static Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);

	private String version = "0.1 beta";
	private String basePath = "http://localhost:8080/nextprot-api-web";
	private List<String> packages = Arrays.asList(new String[]{"org.nextprot.api"});
	private JSONDoc apiDoc;

	@Autowired
	private Environment env;

	public void setVersion(String version) {
		this.version = version;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public @ResponseBody JSONDoc getApi() {

		synchronized (this) {
			if (apiDoc == null) {
				apiDoc = JSONDocUtils.getApiDoc(version, basePath, packages);
			}
		}

		Set<String> contextRoles = NPSecurityContext.getCurrentUserRoles();
		LOGGER.info("Context roles");
		for (String role : contextRoles) {
			LOGGER.info(role);
		}

		// Comparator to order by api name
		Set<ApiDoc> contextApis = new TreeSet<ApiDoc>(new Comparator<ApiDoc>() {
			public int compare(ApiDoc o1, ApiDoc o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		Set<ApiDoc> apis = apiDoc.getApis();
		for (ApiDoc api : apis) {
			boolean devMode = false;
			if (env != null) {
				String[] pfs = env.getActiveProfiles();
				if (pfs != null) {
					for (String e : pfs) {
						if (e.equalsIgnoreCase("dev")) {
							devMode = true;
							break;
						}
					}
				}
			}
			if (api.getRole().equals("ROLE_ANONYMOUS") || contextRoles.contains(api.getRole()) || devMode) {
				contextApis.add(api);
			}
		}

		JSONDoc contextJSONDoc = new JSONDoc(version, basePath);
		contextJSONDoc.setApis(contextApis);
		contextJSONDoc.setObjects(apiDoc.getObjects());

		return contextJSONDoc;

	}
}
