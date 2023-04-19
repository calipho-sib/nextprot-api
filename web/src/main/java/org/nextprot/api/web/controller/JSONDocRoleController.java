package org.nextprot.api.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.pojo.*;
import org.jsondoc.core.util.JSONDocType;
import org.jsondoc.springmvc.controller.JSONDocController;
import org.jsondoc.springmvc.scanner.SpringJSONDocScanner;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.export.format.EntryBlock;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.web.service.impl.ExportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;

@Controller
public class JSONDocRoleController extends JSONDocController {

	private final static Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);

	private JSONDoc jsonDoc;

	public JSONDocRoleController() {
		super(null, "", null);
	}

	@Autowired
	private Environment env;

	@Autowired
	private ReleaseInfoService releaseInfoService;

	private static ApiMethodDoc cloneMethodDoc(ApiMethodDoc met, String path, String description, boolean xmlSupported, boolean copyPathParam) {
		ApiMethodDoc m = new ApiMethodDoc();
		m.setQueryparameters(met.getQueryparameters());
		Set<String> produces = new HashSet<String>();
		if(xmlSupported) produces.add(MediaType.APPLICATION_XML_VALUE);
		produces.add(MediaType.APPLICATION_JSON_VALUE);
		m.setProduces(produces);
		m.setConsumes(met.getConsumes());
		Set<ApiParamDoc> set = new HashSet<ApiParamDoc>();
		
		if(copyPathParam){
			ApiParamDoc apd = met.getPathparameters().iterator().next();
			//	public ApiParamDoc(String name, String description, JSONDocType jsondocType, String required, String[] allowedvalues, String format, String defaultvalue) {
			set.add(new ApiParamDoc(apd.getName(), description, apd.getJsondocType(), apd.getRequired(), apd.getAllowedvalues(), apd.getFormat(), apd.getDefaultvalue()));
			m.setPathparameters(set);
		}
		
		m.setQueryparameters(met.getQueryparameters());
		m.setPath(path);
		m.setVerb(ApiVerb.GET);

		return m;
	}

	@PostConstruct
	public void init() {

		List<String> packages = new ArrayList<String>();
		packages.addAll(Arrays.asList(new String[] { 
				"org.nextprot.api.commons", 
				"org.nextprot.api.core", 
				"org.nextprot.api.isoform", 
				"org.nextprot.api.rdf", 
				"org.nextprot.api.solr", 
				"org.nextprot.api.user",
				"org.nextprot.api.web",
				"org.nextprot.api.blast" }));

		String version = releaseInfoService.findReleaseVersions().getApiRelease();
		for (String profile : env.getActiveProfiles()) {
			if (profile.equalsIgnoreCase("build")) {
				packages.add("org.nextprot.api.build");
				packages.add("org.nextprot.api.tasks");
				packages.add("org.nextprot.api.etl");
				break;
			}
		}

		jsonDoc = new SpringJSONDocScanner().getJSONDoc(version, "", packages);
		for (Set<ApiDoc> apiDocs : jsonDoc.getApis().values()) {

			{ //////////////////////////// Appends documentation to Entry Controller ////////////////////////////

				ApiMethodDoc met = getMethodOfType(apiDocs, "Entry");
				for (ApiDoc apiDoc : filterApiDocsFor(apiDocs, "Entry")) {

					// adding blocks
					for (EntryBlock block : EntryBlock.values()) {
						if (!block.equals(EntryBlock.FULL_ENTRY)) {
							String name = block.name().toLowerCase().replaceAll("_", "-");
							String path = "/entry/{entry}/" + StringUtils.camelToKebabCase(name);
							apiDoc.getMethods().add(cloneMethodDoc(met, path, "", true, true));
						}
					}

					// adding subparts
					for (AnnotationCategory model : AnnotationCategory.values()) {

						if(!model.equals(AnnotationCategory.VIRTUAL_ANNOTATION) && !model.isChildOf(AnnotationCategory.VIRTUAL_ANNOTATION)){

							String name = model.getApiTypeName();

							if("FamilyName".equals(name) || "Name".equals(name)) {
								continue;
							}

							String path = "/entry/{entry}/" + StringUtils.camelToKebabCase(name);
							String description = "Exports only the " + name + " from an entry, located on the hierarchy: " + model.getHierarchy();

							ApiMethodDoc methodDoc = cloneMethodDoc(met, path, description, true, true);

							if (model == AnnotationCategory.EXPRESSION_PROFILE) {
								methodDoc.getProduces().add(NextprotMediaType.TSV_MEDIATYPE_VALUE);
							}

							methodDoc.getQueryparameters().add(buildOptionalQueryParameter("term-child-of", "An optional cv term filter: export annotations " +
									"which cv term matches the cv term parameter or one of its descendants"));

							methodDoc.getQueryparameters().add(buildOptionalQueryParameter("property-name", "An optional property name filter: export annotations " +
									"which contains this property name (see also property-value filter)"));

							methodDoc.getQueryparameters().add(buildOptionalQueryParameter("property-value", "An optional property value filter: export annotations " +
									"which contains the property name with this property value or accession (see also property-name filter)"));

							apiDoc.getMethods().add(methodDoc);
						}
					}

				}

			}

		}
	}

	private ApiParamDoc buildOptionalQueryParameter(String name, String desc) {

		return new ApiParamDoc(name, desc, new JSONDocType("string"), "false", new String[]{""}, "", "");
	}

	private static ApiMethodDoc getMethodOfType(Collection<ApiDoc> apiDocs, String type) {

		// Adds terminology
		for (ApiDoc apiDoc : apiDocs) {
			if (apiDoc.getName().equals(type) && apiDoc.getMethods() != null && !apiDoc.getMethods().isEmpty()) {
				return apiDoc.getMethods().iterator().next();
			}
		}

		return null;

	}

	private static Collection<ApiDoc> filterApiDocsFor(Collection<ApiDoc> apiDocs, String type) {

		List<ApiDoc> docs = new ArrayList<ApiDoc>();
		for (ApiDoc apiDoc : apiDocs) {
			if (apiDoc.getName().equals(type)) {
				docs.add(apiDoc);
			}
		}

		return docs;

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
		for (Entry<String, Set<ApiDoc>> apis : jsonDoc.getApis().entrySet()) {

			// For each class annotation (ApiDoc)
			Set<ApiDoc> contextApiDocs = new TreeSet<ApiDoc>();
			for (ApiDoc apiDoc : apis.getValue()) {

				// Check authorization at class level
				if (apiDoc.getAuth() == null || apiDoc.getAuth().equals("ROLE_ANONYMOUS") || (contextRoles != null && !Collections.disjoint(contextRoles, apiDoc.getAuth().getRoles()))) {

					// For each method annotation (ApiMethodDoc)
					Set<ApiMethodDoc> contextApiMethodDocs = new TreeSet<ApiMethodDoc>();
					for (ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {

						//Add Iso Mapper Documentation if the user it is an ADMIN (the service doesn't need authentication to work though, it is just for documentation)
						if(apiDoc.getName().equalsIgnoreCase("Isoform Mapping")){
							
							if((contextRoles != null) && (contextRoles.contains("ROLE_ADMIN"))){
								contextApiMethodDocs.add(apiMethodDoc);
							}
							
						}// Check authorization at method level
						else if (apiMethodDoc.getAuth() == null || apiMethodDoc.getAuth().equals("ROLE_ANONYMOUS") || contextRoles != null
								&& !Collections.disjoint(contextRoles, apiMethodDoc.getAuth().getRoles())) {
							contextApiMethodDocs.add(apiMethodDoc);
						}
						
						
					}
					if (!contextApiMethodDocs.isEmpty()) {
						// Create a copy of apiDoc but with methods according to
						// contextRoles
						ApiDoc tmpApiDoc = new ApiDoc();
						tmpApiDoc.setDescription(apiDoc.getDescription());
						tmpApiDoc.setName(apiDoc.getName());
						tmpApiDoc.setGroup(apiDoc.getGroup());
						tmpApiDoc.setMethods(contextApiMethodDocs);
						tmpApiDoc.setSupportedversions(apiDoc.getSupportedversions());
						tmpApiDoc.setAuth(apiDoc.getAuth());

						contextApiDocs.add(tmpApiDoc);
					}
				}
				

			}
			if (!contextApiDocs.isEmpty()) {
				contextApis.put(apis.getKey(), contextApiDocs);
				LOGGER.info("Add \"" + apis.getKey() + "\" Api to the current user");
			}
		}

		for (Entry<String, Set<ApiDoc>> stringSetEntry : contextApis.entrySet()) {
			stringSetEntry.getValue().stream()
				.flatMap(e -> e.getMethods().stream())
				.filter(m -> m.getPath().endsWith(":.+}")) //special case for 1.1.1.1 which should not be considered as extension
				.forEach(m -> {
				m.setPath(m.getPath().replace(":.+", ""));
			});
		}

		JSONDoc contextJSONDoc = new JSONDoc(releaseInfoService.findReleaseVersions().getApiRelease(), "");
		contextJSONDoc.setApis(contextApis);
		contextJSONDoc.setObjects(jsonDoc.getObjects());
		contextJSONDoc.setFlows(jsonDoc.getFlows());

		return contextJSONDoc;
	}

}
