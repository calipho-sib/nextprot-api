package org.nextprot.api.web;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.springmvc.controller.JSONDocController;
import org.nextprot.api.core.service.export.impl.ExportServiceImpl;
import org.springframework.stereotype.Controller;

@Controller
public class JSONDocRoleController extends JSONDocController {


	private final static Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);

	private static String version = "0.1 beta";
	private static String basePath = "http://localhost:8080/nextprot-api-web";
	private static List<String> packages = Arrays.asList(new String[] { "org.nextprot.api" });

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
//	@PostConstruct
//	public void init() {
//
//		apiDoc = JSONDocUtils.getApiDoc(version, basePath, packages);
//		for(ApiDoc a : apiDoc.getApis()) {
//			ApiMethodDoc met = null;
//			if(a.getName().equals("Entry") && (a.getMethods() != null) && (!a.getMethods().isEmpty())){
//				met = a.getMethods().get(0);
//				//System.out.println(met);
//			}
//			
//			if (a.getName().equals("Entry")) {
//				for (AnnotationApiModel model : AnnotationApiModel.values()) {
//					
//					ApiMethodDoc m = new ApiMethodDoc();
//					m.setQueryparameters(met.getQueryparameters());
//					m.setProduces(met.getProduces());
//					m.setConsumes(met.getConsumes());
//					m.setDescription("Exports only the " + model.getDescription() + " from an entry. It locates on the hierarchy: " + model.getHierarchy());
//					m.setPath("/entry/{entry}/" + StringUtils.decamelizeAndReplaceByHyphen(model.getApiTypeName()));
//					m.setVerb(ApiVerb.GET);
//					a.getMethods().add(m);
//				}
//			}
//
//		}
//	}

//	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Override
//	public @ResponseBody JSONDoc getApi() {
//
//		Set<String> contextRoles = NPSecurityContext.getCurrentUserRoles();
//		LOGGER.info("Context roles");
//		for (String role : contextRoles) {
//			LOGGER.info(role);
//		}
//
//		// Comparator to order by api name
//		Set<ApiDoc> contextApis = new TreeSet<ApiDoc>(new Comparator<ApiDoc>() {
//			public int compare(ApiDoc o1, ApiDoc o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
//
//		Set<ApiDoc> apis = apiDoc.getApis();
//		for (ApiDoc api : apis) {
//			boolean devMode = false;
//			if (env != null) {
//				String[] pfs = env.getActiveProfiles();
//				if (pfs != null) {
//					for (String e : pfs) {
//						if (e.equalsIgnoreCase("dev")) {
//							devMode = true;
//							break;
//						}
//					}
//				}
//			}
//			if (api.getRole().equals("ROLE_ANONYMOUS") || contextRoles.contains(api.getRole()) || devMode) {
//				contextApis.add(api);
//			}
//
//			Collections.sort(api.getMethods(), new Comparator<ApiMethodDoc>() {
//				@Override
//				public int compare(ApiMethodDoc o1, ApiMethodDoc o2) {
//					return o1.getPath().compareTo(o2.getPath());
//				}
//			});
//
//		}
//
//		JSONDoc contextJSONDoc = new JSONDoc(version, basePath);
//		contextJSONDoc.setApis(contextApis);
//		contextJSONDoc.setObjects(apiDoc.getObjects());
//
//		return contextJSONDoc;
//
//	}
}
