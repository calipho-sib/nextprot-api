package org.nextprot.api.core.utils.graph;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.graph.DirectedGraph;
import org.nextprot.api.commons.utils.graph.IntGrph;
import org.nextprot.api.core.service.TerminologyService;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * A hierarchy of {@code CvTerm} ids organised in a Directed Acyclic Graph.
 */
public class CvTermGrph extends BaseCvTermGraph implements Serializable {

    public CvTermGrph(TerminologyCv terminologyCv, TerminologyService service) {

        super(terminologyCv, service, IntGrph::new);
    }

    private CvTermGrph(TerminologyCv terminologyCv, DirectedGraph graph) {

        super(terminologyCv, graph);
    }

    @Override
    protected Supplier<CvTermGrph> newSupplier(TerminologyCv terminologyCv, DirectedGraph graph) {

        return () -> new CvTermGrph(terminologyCv, graph);
    }
}
