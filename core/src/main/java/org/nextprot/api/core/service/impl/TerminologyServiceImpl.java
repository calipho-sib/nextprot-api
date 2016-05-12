package org.nextprot.api.core.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.commons.utils.Tree.Node;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
class TerminologyServiceImpl implements TerminologyService {

	private static final Log LOGGER = LogFactory.getLog(TerminologyServiceImpl.class);

	@Autowired
	private TerminologyDao terminologyDao;

	@Override
	@Cacheable("terminology-by-accession")
	public Terminology findTerminologyByAccession(String accession) {
		return terminologyDao.findTerminologyByAccession(accession);
	}

	@Override
	public List<Terminology> findTerminologByTitle(String title) {
		return terminologyDao.findTerminologByTitle(title);
	}

	@Override
	public List<Terminology> findTerminologyByName(String name) {
		return terminologyDao.findTerminologyByName(name);
	}

	private static void appendAncestor(Node<Terminology> node, Set<String> result) {
		result.add(node.getValue().getAccession());
		if (node.getParents() != null && !node.getParents().isEmpty()) {
			for (Node<Terminology> parent : node.getParents()) {
				appendAncestor(parent, result);
			}
		}

	}

	@Cacheable("terminology-ancestor-sets")
	public Set<String> getAncestorSets(Tree<Terminology> tree, String accession) {
		Set<String> result = new TreeSet<String>();
		List<Node<Terminology>> nodes = TerminologyUtils.getNodeListByName(tree, accession);

		for (Node<Terminology> node : nodes) {
			appendAncestor(node, result);
		}

		result.remove(accession); // a term is not it's own ancestor
		return result;
	}

	@Override
	@Cacheable("terminology-tree-depth")
	public List<Tree<Terminology>> findTerminologyTreeList(TerminologyCv terminologyCv, int maxDepth) {

		List<Terminology> terms = findTerminologyByOntology(terminologyCv.name());
		List<Tree<Terminology>> result = TerminologyUtils.convertTerminologyListToTreeList(terms, maxDepth);
		return result;
	}

	@Override
	@Cacheable("terminology-by-ontology")
	public List<Terminology> findTerminologyByOntology(String ontology) {
		List<Terminology> terms = terminologyDao.findTerminologyByOntology(ontology);
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<Terminology>().addAll(terms).build();

	}

	@Override
	@Cacheable("terminology-all")
	public List<Terminology> findAllTerminology() {
		List<Terminology> terms = terminologyDao.findAllTerminology();
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<Terminology>().addAll(terms).build();

	}

	@Override
	@Cacheable("enzyme-terminology") // TODO there should be an utiliy method on
										// entry to get the enzymes...
	public List<Terminology> findEnzymeByMaster(String entryName) {
		Set<String> accessions = new HashSet<String>(terminologyDao.findEnzymeAcsByMaster(entryName));
		if (!accessions.isEmpty()) { // is found accessions gets the
										// corresponding terminology
			List<Terminology> terms = terminologyDao.findTerminologyByAccessions(accessions);
			// returns a immutable list when the result is cacheable (this
			// prevents modifying the cache, since the cache returns a
			// reference) copy on read and copy on write is too much time
			// consuming
			return new ImmutableList.Builder<Terminology>().addAll(terms).build();
		} else
			return new ImmutableList.Builder<Terminology>().build(); // returns
																		// empty
																		// list
	}

	@Override
	public List<Terminology> findTerminologyByAccessions(Set<String> terminologyAccessions) {
		List<Terminology> terms = terminologyDao.findTerminologyByAccessions(terminologyAccessions);
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<Terminology>().addAll(terms).build();

	}

	@Override
	@Cacheable("terminology-names")
	public List<String> findTerminologyNamesList() {
		return new ImmutableList.Builder<String>().addAll(terminologyDao.findTerminologyNamesList()).build();
	}

}
