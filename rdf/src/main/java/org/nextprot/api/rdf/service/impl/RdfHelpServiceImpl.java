package org.nextprot.api.rdf.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.domain.RdfConstants;
import org.nextprot.api.rdf.domain.RdfTypeInfo;
import org.nextprot.api.rdf.domain.TripleInfo;
import org.nextprot.api.rdf.service.RdfHelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Lazy
@Service
public class RdfHelpServiceImpl implements RdfHelpService {

	private List<String> completeSetOfValuesForTypes = Arrays.asList(":Source", ":Database", ":SubcellularLocation", ":NextprotTissues");
	private List<String> completeSetOfValuesForLiteral = Arrays.asList("NextprotTissues/rdfs:label", ":SubcellularLocation/rdfs:comment");

	@Autowired
	private RdfHelpCacheableServiceImpl rdfHelpServiceCached;

	private final int NUMBER_THREADS = 10;

	@Override
	public List<RdfTypeInfo> getRdfTypeFullInfoList() {

		Set<String> rdfTypesNames = rdfHelpServiceCached.getAllRdfTypesNames();
		List<Future<RdfTypeInfo>> rdfFutureTypes = new ArrayList<Future<RdfTypeInfo>>();
		List<RdfTypeInfo> rdfTypes = Collections.synchronizedList(new ArrayList<RdfTypeInfo>());

		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		for (String rdfTypeName : rdfTypesNames) {
			Future<RdfTypeInfo> futureRdfTypeInfo = executor.submit(new FillRdfTypeInfoTask(this, rdfTypeName));
			rdfFutureTypes.add(futureRdfTypeInfo);
		}

		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new NextProtException(e.getLocalizedMessage());
		}

		for (Future<RdfTypeInfo> futureRdfTypeInfo : rdfFutureTypes) {
			try {
				rdfTypes.add(futureRdfTypeInfo.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		// now populate parent and parent triples of each type
		for (RdfTypeInfo rti : rdfTypes) {
			for (RdfTypeInfo parent : rdfTypes) {
				List<TripleInfo> triples = parent.findTriplesWithObjectType(rti.getTypeName());
				if (triples.size() > 0) {
					rti.addParent(parent.getTypeName());
					for (TripleInfo triple : triples)
						rti.addParentTriple(triple);
				}
			}
		}

		Map<String, RdfTypeInfo> fullMap = new HashMap<String, RdfTypeInfo>();
		for (RdfTypeInfo rti : rdfTypes) {
			fullMap.put(rti.getTypeName(), rti);
		}

		buildPathToOrigin(fullMap, fullMap.get(":Entry"), "?entry ", 0);

		return rdfTypes;
	}

	private static void buildPathToOrigin(final Map<String, RdfTypeInfo> fullMap, RdfTypeInfo currentEntry, String currentPath, int currenDepth) {
		if (currenDepth > 20)
			return;
		for (TripleInfo childTripleInfo : currentEntry.getTriples()) {
			RdfTypeInfo childTypeInfo = fullMap.get(childTripleInfo.getObjectType());
			if (childTypeInfo != null && !currentPath.contains(childTripleInfo.getPredicate()) && !childTripleInfo.getPredicate().equals(":interaction")) {
				String nextPath = currentPath + childTripleInfo.getPredicate();
				childTypeInfo.addPathToOrigin(nextPath);
				buildPathToOrigin(fullMap, childTypeInfo, nextPath + "/", currenDepth + 1);
			}
		}
	}


	@Override
	public RdfTypeInfo getRdfTypeFullInfo(String rdfTypeName) {

		RdfTypeInfo rdfTypeInfo = new RdfTypeInfo();
		rdfTypeInfo.setTypeName(rdfTypeName);

		Map<String, String> properties = rdfHelpServiceCached.getRdfTypeProperties(rdfTypeName);
		
		if(!properties.isEmpty()){
			rdfTypeInfo.setTypeName(properties.get("rdfType"));
			rdfTypeInfo.setRdfsLabel(properties.get("label"));
			rdfTypeInfo.setRdfsComment(properties.get("comment"));
			rdfTypeInfo.setInstanceCount(Integer.valueOf(properties.get("instanceCount")));
			rdfTypeInfo.setInstanceSample(properties.get("instanceSample"));
		}


		List<TripleInfo> triples = rdfHelpServiceCached.getTripleInfoList(rdfTypeInfo.getTypeName());

		Set<String> values = null;
		if (completeSetOfValuesForTypes.contains(rdfTypeInfo.getTypeName())) {
			values = rdfHelpServiceCached.getRdfTypeValues(rdfTypeName, Integer.MAX_VALUE);
		} else {
			values = rdfHelpServiceCached.getRdfTypeValues(rdfTypeName, 20);
		}

		rdfTypeInfo.setValues(values);

		for (TripleInfo triple : triples) {
			rdfTypeInfo.addTriple(triple);
			if (triple.isLiteralType() && (!triple.getObjectType().equals(RdfConstants.BLANK_OBJECT_TYPE))) {
				String typeLiteral = rdfTypeName + "/" + triple.getPredicate();
				Set<String> exampleValues = null;
				if (completeSetOfValuesForLiteral.contains(typeLiteral)) {
					exampleValues = rdfHelpServiceCached.getValuesForTriple(rdfTypeName, triple.getPredicate(), Integer.MAX_VALUE);
				} else {
					exampleValues = rdfHelpServiceCached.getValuesForTriple(rdfTypeName, triple.getPredicate(), 50);
				}

				triple.setValues(exampleValues);
			}
		}

		return rdfTypeInfo;

	}

	@Override
	public List<String> getRdfTypeValues(String rdfTypeName) {
		return new ArrayList<String>(rdfHelpServiceCached.getRdfTypeValues(rdfTypeName, Integer.MAX_VALUE));
	}
	
	
	//Task
	private static class FillRdfTypeInfoTask implements Callable<RdfTypeInfo> {

		private String rdfType = null;

		private RdfHelpService rdfTypeInfoService;

		public FillRdfTypeInfoTask(RdfHelpService rdfTypeInfoService, String rdfType) {
			this.rdfType = rdfType;
			this.rdfTypeInfoService = rdfTypeInfoService;
		}

		@Override
		public RdfTypeInfo call() {
			System.out.println("Calling " + rdfType);
			return rdfTypeInfoService.getRdfTypeFullInfo(rdfType);
		}


	}


}
