package org.nextprot.api.core.service.impl;

import com.google.common.collect.ImmutableList;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.Tree.Node;
import org.nextprot.api.core.dao.TerminologyDao;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.CvTermGraph;
import org.nextprot.api.core.service.CvTermGraphService;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class TerminologyServiceImpl implements TerminologyService {

    @Autowired
	private TerminologyDao terminologyDao;
	@Autowired
	private CvTermGraphService cvTermGraphService;

	@Override
	public CvTerm findCvTermByAccession(String cvTermAccession) {
		return terminologyDao.findTerminologyByAccession(cvTermAccession);
	}

	private static void appendAncestor(Node<CvTerm> node, Set<String> result) {
		result.add(node.getValue().getAccession());
		if (node.getParents() != null && !node.getParents().isEmpty()) {
			for (Node<CvTerm> parent : node.getParents()) {
				appendAncestor(parent, result);
			}
		}

	}

	@Override
	@Cacheable(value = "term-by-ontology", sync = true)
	public List<CvTerm> findCvTermsByOntology(String ontology) {
		List<CvTerm> terms = terminologyDao.findTerminologyByOntology(ontology);
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<CvTerm>().addAll(terms).build();
	}

	@Override
	@Cacheable(value = "all-terms", sync = true)
	public List<CvTerm> findAllCVTerms() {
		List<CvTerm> terms = terminologyDao.findAllTerminology();
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<CvTerm>().addAll(terms).build();

	}

	@Override
	@Cacheable(value = "enzyme-terminology", sync = true) // TODO there should be an utiliy method on
										// entry to get the enzymes...
	public List<CvTerm> findEnzymeByMaster(String entryName) {
		Set<String> accessions = new HashSet<String>(terminologyDao.findEnzymeAcsByMaster(entryName));
		if (!accessions.isEmpty()) { // is found accessions gets the
										// corresponding terminology
			List<CvTerm> terms = terminologyDao.findTerminologyByAccessions(accessions);
			// returns a immutable list when the result is cacheable (this
			// prevents modifying the cache, since the cache returns a
			// reference) copy on read and copy on write is too much time
			// consuming
			return new ImmutableList.Builder<CvTerm>().addAll(terms).build();
		} else
			return new ImmutableList.Builder<CvTerm>().build(); // returns
																		// empty
																		// list
	}

	@Override
	public List<CvTerm> findCvTermsByAccessions(Set<String> terminologyAccessions) {
		List<CvTerm> terms = terminologyDao.findTerminologyByAccessions(terminologyAccessions);
		// returns a immutable list when the result is cacheable (this prevents
		// modifying the cache, since the cache returns a reference) copy on
		// read and copy on write is too much time consuming
		return new ImmutableList.Builder<CvTerm>().addAll(terms).build();

	}

	@Override
	@Cacheable(value = "terminology-names", sync = true)
	public List<String> findTerminologyNamesList() {
		return new ImmutableList.Builder<String>().addAll(terminologyDao.findTerminologyNamesList()).build();
	}

	// https://github.com/HUPO-PSI/psi-mod-CV/blob/master/PSI-MOD.obo
	@Override
	public Optional<String> findPsiModName(String cvTermAccession) {

		Optional<String> psiModAccessionOpt = findPsiModAccession(cvTermAccession);

		if (!psiModAccessionOpt.isPresent()) {
			return Optional.empty();
		}

		String psiModAccession = psiModAccessionOpt.get();

		InputStream is = getClass().getResourceAsStream("peff/PSI-MOD.obo");

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

			String line;

			while ((line = br.readLine()) != null) {

				// if the psi term is found...
				if (line.startsWith("id: " + psiModAccession)) {
					return findTermName(br);
				}
			}
			// no psi term found
			return Optional.empty();
		} catch (IOException e) {
			throw new NextProtException(e.getMessage()+": cannot find PSI-MOD name for cv term "+cvTermAccession);
		}
	}

	@Override
	public List<CvTerm> getAllAncestorTerms(String cvTermAccession) {

		CvTerm cvTerm = terminologyDao.findTerminologyByAccession(cvTermAccession);
		CvTermGraph graph = cvTermGraphService.findCvTermGraph(TerminologyCv.valueOf(cvTerm.getOntology()));

		return Arrays.stream(graph.getAncestors(cvTerm.getId().intValue())).boxed()
				.map(graph::getCvTermAccessionById)
				.map(terminologyDao::findTerminologyByAccession)
				.collect(Collectors.toList());
	}

	@Override
	public List<CvTerm> getOnePathToRootTerm(String cvTermAccession) {
		List<CvTerm> path = new ArrayList<>();
		String ac = cvTermAccession;
		while (true) {
			CvTerm t = findCvTermByAccessionOrThrowRuntimeException(ac);
			path.add(t);
			List<String> parents = t.getAncestorAccession();
			if (parents==null || parents.size()==0) break;
			ac = parents.get(0);
		}
		return path;
	}

    private Optional<String> findTermName(BufferedReader br) throws IOException {

		String line;

		while ((line = br.readLine()) != null) {

			// if found the term name
			if (line.startsWith("name: ")) {
				return Optional.of(line.split(": ")[1]);
			}
			// if end of the [term] definition block
			else if (line.matches("\\\\s*")) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
}
