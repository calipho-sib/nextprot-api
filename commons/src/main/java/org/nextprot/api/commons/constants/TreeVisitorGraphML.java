package org.nextprot.api.commons.constants;

import org.nextprot.api.commons.utils.StringUtils;

/**
 * Created by fnikitin on 17/06/15.
 */
public class TreeVisitorGraphML extends AbstractTreeVisitor {

    public TreeVisitorGraphML(String graphName) {

        super(graphName);
    }

    @Override
    protected void writeNode(AnnotationApiModel node) {

        sb.append("\t\t<node id=\"").append(StringUtils.camelToKebabCase(node.getApiTypeName())).append("\"/>\n");
    }

    @Override
    protected void writeEdge(AnnotationApiModel parent, AnnotationApiModel child) {

        sb.append("\t\t<edge id=\"").append(parent.getApiTypeName()).append(".").append(child.getApiTypeName()).append("\" source=\"").append(StringUtils.camelToKebabCase(parent.getApiTypeName()))
                .append("\" target=\"").append(StringUtils.camelToKebabCase(child.getApiTypeName())).append("\"/>\n");
    }

    @Override
    public String asString() {

        StringBuilder content = new StringBuilder();

        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        content.append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" ");
        content.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        content.append("xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
        content.append("\t<graph id=\"").append(getName()).append("\" edgedefault=\"undirected\">\n");
        content.append(sb.toString()).append("\t</graph>\n</graphml>");

        return content.toString();
    }
}