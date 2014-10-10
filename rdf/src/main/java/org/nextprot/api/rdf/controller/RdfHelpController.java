package org.nextprot.api.rdf.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.domain.RdfTypeInfo;
import org.nextprot.api.rdf.domain.TripleInfo;
import org.nextprot.api.rdf.service.RdfHelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Lazy
@Controller
@Api(name = "RdfHelp", description = "Method to retrieve help on the complete or partial RDF schema of neXtProt from data.", role ="ROLE_RDF")
public class RdfHelpController {

	@Autowired
	private RdfHelpService service;

	/**
	 * Full info of a single rdf:type
	 * 
	 * @param rdfType
	 * @param model
	 * @return
	 */
	@ApiMethod(path = "/rdf/help/type/{rdfType}", verb = ApiVerb.GET, description = "Gets a description and properties of a rdf type", produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping("/rdf/help/type/{rdfType}")
	@ResponseBody
	public RdfTypeInfo helpFullInfo(
			@ApiParam(name = "rdfType", description = "The description and properties related to a rdf:type object in neXtProt RDF schema. For example, the entities :Entry :Isoform ...", allowedvalues = { ":Entry" }) @PathVariable("rdfType") String rdfType,
			Model model) {
		try {
			RdfTypeInfo typ;
			typ = this.service.getRdfTypeFullInfo(rdfType);
			model.addAttribute("result", typ);
			return typ;

		} catch (Exception e) {
			e.printStackTrace();
			throw new NextProtException(e.getMessage());
		}
	}

	@ApiMethod(path = "/rdf/help/type/all", verb = ApiVerb.GET, description = "Gets a description and properties of all rdf types. WARNING: may require a lot of time.", produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping("/rdf/help/type/all")
	@ResponseBody
	public List<RdfTypeInfo> helpFullInfo(Model model) {
		List<RdfTypeInfo> list = this.service.getRdfTypeFullInfoList();
		model.addAttribute("result", list);
		return list;
	}

	@RequestMapping("/rdf/help/type/{rdfType}/values")
	@ResponseBody
	public List<String> helpFullInfoValues(@PathVariable("rdfType") String rdfType, Model model) {
		try {
			return this.service.getRdfTypeValues(rdfType);
		} catch (Exception e) {
			throw new NextProtException(e.getMessage());
		}
	}

	@RequestMapping("/rdf/help/visgraphp")
	public String visgraphp(Model model) {

		Map<String, RdfTypeInfo> map = new HashMap<String, RdfTypeInfo>();
		List<String> names = new ArrayList<String>();

		for (RdfTypeInfo rti : this.service.getRdfTypeFullInfoList()) {
			map.put(rti.getTypeName(), rti);
			names.add(rti.getTypeName());
		}

		model.addAttribute("visutils", new VisUtils());
		model.addAttribute("map", map);
		model.addAttribute("names", names);

		return "visgraphp";
	}

	@RequestMapping("/rdf/help/visgraph")
	public String visgraph(Model model) {

		Map<String, RdfTypeInfo> map = new HashMap<String, RdfTypeInfo>();
		List<String> names = new ArrayList<String>();

		for (RdfTypeInfo rti : this.service.getRdfTypeFullInfoList()) {
			map.put(rti.getTypeName(), rti);
			if (!rti.getTypeName().startsWith(":Isoform") && !rti.getTypeName().startsWith(":Evidence")) {
				names.add(rti.getTypeName());
			} else
				System.out.println("Skipped for" + rti);
		}

		model.addAttribute("visutils", new VisUtils());
		model.addAttribute("map", map);
		model.addAttribute("names", names);

		return "visgraph";
	}

	// Example http://localhost:8080/nextprot-api/rdf/help/visgraph/:Entry,:Gene
	@RequestMapping("/rdf/help/visgraph/{rdfType}")
	public String visgraph(@PathVariable("rdfType") String rdfType, Model model) {

		Map<String, RdfTypeInfo> map = new HashMap<String, RdfTypeInfo>();
		for (RdfTypeInfo rti : this.service.getRdfTypeFullInfoList()) {
			map.put(rti.getTypeName(), rti);
		}

		model.addAttribute("visutils", new VisUtils());
		model.addAttribute("map", map);
		model.addAttribute("names", Arrays.asList(rdfType.split(",")));

		return "visgraph";
	}

	// Example http://localhost:8080/nextprot-api/rdf/help/visgraph/:Entry,:Gene
	@RequestMapping("/rdf/help/visgraphp/{rdfType}")
	public String visgraphp(@PathVariable("rdfType") String rdfType, Model model) {

		Map<String, RdfTypeInfo> map = new HashMap<String, RdfTypeInfo>();
		for (RdfTypeInfo rti : this.service.getRdfTypeFullInfoList()) {
			map.put(rti.getTypeName(), rti);
		}

		model.addAttribute("visutils", new VisUtils());
		model.addAttribute("map", map);
		model.addAttribute("names", Arrays.asList(rdfType.split(",")));

		return "visgraphp";
	}

	public static class VisUtils {
		private int n = 6666;
		Set<Integer> ids = new TreeSet<Integer>();
		Map<String, Integer> mapIds = new HashMap<String, Integer>();
		
		public void addId(int id) {
			ids.add(id);
		}

		public Integer getMaxDepth(RdfTypeInfo rti) {
			int max = 0;
			for (String p : rti.getPathToOrigin()) {
				max = Math.max(max, getDepth(p));
			}
			return max;
		}

		public Integer getTripleId(TripleInfo t) {
			String key = t.getSubjectType() + "" + t.getPredicate();
			if (!mapIds.containsKey(key)) {
				mapIds.put(key, n++);
			}
			return mapIds.get(key);
		}

		public Integer getNodeId(String currentNode, String relativePath, String _path) {
			System.out.println("CN:" + currentNode + " RP:" + relativePath + " P:" + _path);

			String path = _path.replace("/", "");

			if (currentNode.equals("Entry"))
				return getPathId(currentNode);

			if (currentNode.equals("isoform")) {
				String afterNode = path.replace(relativePath, "").replace(currentNode, "");
				int ni = afterNode.indexOf(":");
				int nextIndex = afterNode.indexOf(":", ni+1);
				if(nextIndex == -1) {
					nextIndex = ni;
				}
				if(ni == -1) {
					nextIndex = 0;
				}

				
				System.out.println("AN" + afterNode + " ANS" + afterNode.substring(nextIndex));
				return getPathId(currentNode + afterNode.substring(nextIndex));
			}
			
			String currentAndAfterNode = path.replace(relativePath, "");
			System.out.println(currentAndAfterNode + getPathId(currentAndAfterNode));
			System.out.println();
			return getPathId(currentAndAfterNode);
		}

		public Integer getPathId(String path) {
			if (!mapIds.containsKey(path)) {
				mapIds.put(path, n++);
			}
			return mapIds.get(path);
		}

		public boolean isLast(String path, String absolutePath) {
			return path.replace("/", "").equals(absolutePath);
		}

		public Integer getEdgeId(Integer id1, Integer id2) {
			String s = id1.toString() + id2.toString();
			return Integer.valueOf(s);
		}

		public Integer getDepth(String p) {
			return p.split(":").length;
		}

		public String getSimplePath(String path) {
			int n = StringUtils.reverse(path).indexOf(":");
			if (n == -1)
				return "Entry";
			return path.substring(path.length() - n);
		}

		public List<String> getPathArray(String path) {
			String s = path.replace("?entry ", "");
			String ss[] = s.split("/");
			List<String> r = new ArrayList<String>();
			String current = "?entry";
			current += " ";
			r.add(current);
			for (String p : ss) {
				current += p;
				r.add(current);
			}
			return r;
		}

		public boolean contains(int id) {
			return ids.contains(id);
		}
		
		private Set<String> edges = new TreeSet<String>();
		public String getEdge (Integer from, Integer to, String label){
			String key = null;
			if(from < to){
				key = from + "" + to;
			}else key = to + "" + from;
			
			if(edges.contains(key)){
				return "";
			}else {
				edges.add(key);
				return "edges.push({from: " + from + ", to: " + to + ", label: '" + label + "'});";
			}
		
		}
		
	}

	/*
	 * @ApiMethod(path = "/rdf/help/type/{rdfType}/name", verb = ApiVerb.GET, description = "Gets a description of a rdf type", produces = { MediaType.APPLICATION_JSON_VALUE })
	 * @RequestMapping("/rdf/help/type/{rdfType}/name")
	 * @ResponseBody
	 * public RdfTypeInfo helpNames(
	 * @ApiParam(name = "rdfType", description = "The name of of a rdf:type object in neXtProt RDF schema. For example, the entities :Entry :Isoform ...", allowedvalues = { ":Entry" }) @PathVariable("rdfType") String rdfType,
	 * Model model) {
	 * RdfTypeInfo typ = this.service.getRdfTypeName(rdfType);
	 * model.addAttribute("result", typ);
	 * return typ;
	 * }
	 * @ApiMethod(path = "/rdf/help/type/{rdfType}/triplets", verb = ApiVerb.GET, description = "Gets the list of typical triplets found when the subject has rdf:type rdfType", produces = { MediaType.APPLICATION_JSON_VALUE })
	 * @RequestMapping("/rdf/help/type/{rdfType}/triplets")
	 * @ResponseBody
	 * public List<TripleInfo> helpPreds(
	 * @ApiParam(name = "rdfType", description = "The name of of a rdf:type object in neXtProt RDF schema. For example, the entities :Entry :Isoform ...", allowedvalues = { ":Entry" }) @PathVariable("rdfType") String rdfType,
	 * Model model) {
	 * List<TripleInfo> types = this.service.getTripleInfoList(rdfType);
	 * model.addAttribute("result", types);
	 * return types;
	 * }
	 */
}
