package org.nextprot.api.core.utils.graph;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.graph.DirectedGraph;
import org.nextprot.api.commons.utils.graph.IntGraph;
import org.nextprot.api.core.service.TerminologyService;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * A hierarchy of {@code CvTerm} ids organised in a Directed Acyclic Graph.
 */
public class CvTermGraph extends BaseCvTermGraph implements Serializable {

    public CvTermGraph(TerminologyCv terminologyCv, TerminologyService service) {

        super(terminologyCv, service, IntGraph::new);
    }

    private CvTermGraph(TerminologyCv terminologyCv, DirectedGraph graph) {

        super(terminologyCv, graph);
    }

    @Override
    protected Supplier<CvTermGraph> newSupplier(TerminologyCv terminologyCv, DirectedGraph graph) {

        return () -> new CvTermGraph(terminologyCv, graph);
    }

    /*

    {
    "graph": {
        "metadata": {
        },
        "nodes": [
            {
                "id": "0",
                "label": "node label(0)",
                "metadata": { }
            },
            {
                "id": "1",
                "label": "node label(1)",
                "metadata": { }
            }
        ],
        "edges": [
            {
                "tail": "0",
                "relation": "edge relationship",
                "head": "1",
                "label": "edge label",
                "metadata": { }
            }
        ]
    }
}

     */
}
