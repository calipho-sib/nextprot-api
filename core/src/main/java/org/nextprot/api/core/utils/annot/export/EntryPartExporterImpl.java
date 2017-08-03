package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;

import java.util.*;

import static org.nextprot.api.core.utils.annot.export.EntryPartExporter.Header.*;

/**
 * Export data depending on the type of annotation to export (expression-profile, variants, ...)
 */
public class EntryPartExporterImpl implements EntryPartExporter {

    private final Map<Header, Integer> headers;
    private final List<Header> headerList;

    private EntryPartExporterImpl(Builder builder) {

        headers = builder.headers;
        headerList = buildList(headers);
    }

    public static EntryPartExporterImpl fromSubPart(String... subParts) {

        Builder builder = new Builder();

        for (String subPart : subParts) {

            if ("expression-profile".equals(subPart)) {
                builder.addColumns(8, EXPRESSION_LEVEL, STAGE_ACCESSION, STAGE_NAME);
            }
        }

        return builder.build();
    }

    private static List<Header> buildList(Map<Header, Integer> headers) {

        Header[] a = new Header[headers.size()];

        headers.forEach((key, value) -> a[value] = key);

        return Arrays.asList(a);
    }

    @Override
    public List<Header> exportHeaders() {

        return headerList;
    }

    @Override
    public List<Row> exportRows(Entry entry) {

        List<Row> rows = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotations()) {

            collectRows(rows, entry, annotation);
        }

        return rows;
    }

    private void collectRows(List<Row> rows, Entry entry, Annotation annotation) {

        for (AnnotationEvidence evidence : annotation.getEvidences()) {

            Row row = new Row(headerList.size());

            setRowValue(row, ENTRY_ACCESSION,     entry.getUniqueName());
            setRowValue(row, CATEGORY,            StringUtils.camelToKebabCase(annotation.getApiTypeName()));
            setRowValue(row, TERM_ACCESSION,      annotation.getCvTermAccessionCode());
            setRowValue(row, TERM_NAME,           annotation.getCvTermName());
            setRowValue(row, ECO_ACCESSION,       evidence.getEvidenceCodeAC());
            setRowValue(row, ECO_NAME,            evidence.getEvidenceCodeName());
            setRowValue(row, QUALITY,             evidence.getQualityQualifier());
            setRowValue(row, NEGATIVE,            String.valueOf(evidence.isNegativeEvidence()));
            setRowValue(row, EXPRESSION_LEVEL,    evidence.getExpressionLevel());
            setRowValue(row, SOURCE,              evidence.getAssignedBy());
            setRowValue(row, URL,                 entry.getXref(evidence.getResourceId()).map(DbXref::getResolvedUrl).orElse("null"));
            setExperimentalContextRowValues(row, entry, evidence);

            rows.add(row);
        }
    }

    private void setExperimentalContextRowValues(Row row , Entry entry, AnnotationEvidence evidence) {

        ExperimentalContext ec = entry.getExperimentalContext(evidence.getExperimentalContextId())
                .orElseThrow(() -> new NextProtException("missing experimental context for " + evidence.getEvidenceCodeAC()));

        if (ec.getDevelopmentalStage() != null) {
            setRowValue(row, STAGE_ACCESSION, ec.getDevelopmentalStageAC());
            setRowValue(row, STAGE_NAME, ec.getDevelopmentalStage().getName());
        }

        if (ec.getCellLine() != null) {
            setRowValue(row, CELL_LINE_ACCESSION, ec.getCellLineAC());
            setRowValue(row, CELL_LINE_NAME, ec.getCellLine().getName());
        }

        if (ec.getDisease() != null) {
            setRowValue(row, DISEASE_ACCESSION, ec.getDiseaseAC());
            setRowValue(row, DISEASE_NAME, ec.getDisease().getName());
        }

        if (ec.getOrganelle() != null) {
            setRowValue(row, ORGANELLE_ACCESSION, ec.getOrganelleAC());
            setRowValue(row, ORGANELLE_NAME, ec.getOrganelle().getName());
        }
    }

    private void setRowValue(Row row, Header header, String value) {

        if (!headerList.contains(header)) {
            return;
        }

        row.setValue(headers.get(header), value);
    }

    /**
     * Generic builder with common and custom fields to export
     */
    static class Builder {

        private final Map<Header, Integer> headers = new HashMap<>();

        Builder() {
            headers.put(ENTRY_ACCESSION, 0);
            headers.put(CATEGORY,        1);
            headers.put(TERM_ACCESSION,  2);
            headers.put(TERM_NAME,       3);
            headers.put(QUALITY,         4);
            headers.put(ECO_ACCESSION,   5);
            headers.put(ECO_NAME,        6);
            headers.put(NEGATIVE,        7);
            headers.put(SOURCE,         -2);
            headers.put(URL,            -1);
        }

        Builder addColumns(Header... headers) {

            return addColumns(8, headers);
        }

        Builder addColumns(int insertPoint, Header... headers) {

            for (Header header : headers) {

                if (!this.headers.containsKey(header)) {
                    this.headers.put(header, insertPoint++);
                }
            }

            return this;
        }

        private void fixRelativeIndices() {

            for (Header header : headers.keySet()) {

                if (headers.get(header) < 0) {
                    headers.put(header, headers.size()+headers.get(header));
                }
            }
        }

        EntryPartExporterImpl build() {

            fixRelativeIndices();

            return new EntryPartExporterImpl(this);
        }
    }
}
