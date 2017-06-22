package org.nextprot.api.core.service;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.utils.graph.OntologyDAG;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface TerminologyService {

	/**
	 * @return a {@code CvTerm} by accession
	 */
	CvTerm findCvTermByAccession(String accession);

	/**
	 * @return a list of all {@code CvTerm}s of a given ontology
	 */
	List<CvTerm> findCvTermsByOntology(String ontology);

	/**
	 * @deprecated use {@link #findOntologyGraph(TerminologyCv)} instead
	 */
	@Deprecated
	Terminology findTerminology(TerminologyCv terminologyCv);

	/**
	 * @return a Directed Acyclic Graph of {@code CvTerm}s ontology
	 */
	OntologyDAG findOntologyGraph(TerminologyCv terminologyCv);

	/**
	 * @return a {@code CvTerm} by id
	 */
	// TODO: should be implemented here, implementation should be cached, OntologyDAG will refered this service
	//CvTerm findCvTermById(long id);

	/**
	 * Retrieves terms sorted by ontology
	 * 
	 * @return
	 */
	List<CvTerm> findAllCVTerms();

	/**
	 * Gets enzyme terminologies
	 * 
	 * @param entryName
	 * @return
	 */
	List<CvTerm> findEnzymeByMaster(String entryName);

	List<CvTerm> findCvTermsByAccessions(Set<String> terminologyAccessions);

	List<String> findTerminologyNamesList();

	CvTermGraph findAncestorGraphByCvTerm(String accession);

	//TODO TRY TO PLACE THIS ELSEWHERE, BUT PROBABLY SHOULD BE CACHED!
	Set<String> getAncestorSets(List<Tree<CvTerm>> trees, String accession);

	class CvTermGraph implements Serializable {

		private TerminologyCv terminologyCv;

		private Set<Node> nodes;
		private Set<Edge> edges;

		public void setTerminologyCv(TerminologyCv terminologyCv) {
			this.terminologyCv = terminologyCv;
		}

		public TerminologyCv getTerminologyCv() {
			return terminologyCv;
		}

		public void setNodes(Set<Node> nodes) {
			this.nodes = nodes;
		}

		public void setEdges(Set<Edge> edges) {
			this.edges = edges;
		}

		public Set<Node> getNodes() {
			return nodes;
		}

		public Set<Edge> getEdges() {
			return edges;
		}

		static class Node implements Serializable {

			private long cvTermId;
			private String cvTermName;

			public long getCvTermId() {
				return cvTermId;
			}

			public void setCvTermId(long cvTermId) {
				this.cvTermId = cvTermId;
			}

			public String getCvTermName() {
				return cvTermName;
			}

			public void setCvTermName(String cvTermName) {
				this.cvTermName = cvTermName;
			}
		}

		static class Edge implements Serializable {

			private long cvTermIdFrom;
			private long cvTermIdTo;

			public long getCvTermIdFrom() {
				return cvTermIdFrom;
			}

			public void setCvTermIdFrom(long cvTermIdFrom) {
				this.cvTermIdFrom = cvTermIdFrom;
			}

			public long getCvTermIdTo() {
				return cvTermIdTo;
			}

			public void setCvTermIdTo(long cvTermIdTo) {
				this.cvTermIdTo = cvTermIdTo;
			}
		}
	}
}
