package org.nextprot.api.tasks.commons;

import org.nextprot.api.commons.constants.AnnotationCategoryStringWriter;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.StringUtils;

/**
 * Export tree as graphml format (http://graphml.graphdrawing.org/).
 *
 * Created by fnikitin on 17/06/15.
 */
class AnnotationCategoryStringWriterGraphML extends AnnotationCategoryStringWriter {

    AnnotationCategoryStringWriterGraphML(String graphName) {

        super(graphName);
    }

    @Override
    protected void writeNode(AnnotationCategory node, StringBuilder sb) {

        sb.append("\t\t<node id=\"").append(StringUtils.camelToKebabCase(node.getApiTypeName())).append("\"/>\n");
    }

    @Override
    protected void writeEdge(AnnotationCategory parent, AnnotationCategory child, StringBuilder sb) {

        sb.append("\t\t<edge id=\"").append(parent.getApiTypeName()).append(".").append(child.getApiTypeName()).append("\" source=\"").append(StringUtils.camelToKebabCase(parent.getApiTypeName()))
                .append("\" target=\"").append(StringUtils.camelToKebabCase(child.getApiTypeName())).append("\"/>\n");
    }

    protected String getHeader() {

        StringBuilder sb = new StringBuilder();

        sb
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            .append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" ")
            .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
            .append("xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n")
            .append("\t<graph id=\"").append(getName()).append("\" edgedefault=\"undirected\">\n");

        return sb.toString();
    }

    protected String getFooter() {

        return "\t</graph>\n</graphml>";
    }
}