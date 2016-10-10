package org.nextprot.api.tasks.commons;

import org.nextprot.api.commons.constants.AnnotationCategoryStringWriter;
import org.nextprot.api.commons.constants.AnnotationCategory;

/**
 * Export tree as graph dot format (http://www.graphviz.org/content/dot-language).
 *
 * Created by pam on 26/09/16.
 */
class AnnotationCategoryStringWriterLabels extends AnnotationCategoryStringWriter {

    AnnotationCategoryStringWriterLabels(String graphName) {

        super(graphName);
    }

    @Override
    protected void writeEdge(AnnotationCategory parent, AnnotationCategory child, StringBuilder sb) {

    	int level = child.getAllParentsButRoot().size();
    	for (int i =0 ; i<level ; i++) {
            sb.append("\t");
        }
        sb.append(child.getLabel());
        sb.append("\n");
    }
}
