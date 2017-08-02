package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.utils.annot.export.Header.*;

/**
 * Export data depending on the type of annotation to export (expression-profile, variants, ...)
 */
public class EntryPartExporterImpl implements EntryPartExporter {

    private final List<Header> headers;

    private EntryPartExporterImpl(Builder builder) {

        headers = builder.headers;
    }

    public static EntryPartExporterImpl fromSubPart(String subPart) {

        Builder builder = new Builder();

        switch (subPart) {
            case "expression-profile":
                builder.addColumn(STAGE_ACCESSION, STAGE_NAME);
                break;
            //default:
            //    throw new NextProtException("unknown subpart "+subPart);
        }

        return builder.build();
    }

    @Override
    public List<Header> exportHeaders() {
        return headers;
    }

    public List<Row> exportRows(Entry entry) {

        List<Row> rows = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotations()) {

            writeEvidenceRows(rows, entry, annotation);
        }

        return rows;
    }

    private void writeEvidenceRows(List<Row> rows, Entry entry, Annotation annotation) {

        for (AnnotationEvidence evidence : annotation.getEvidences()) {

            List<String> row = new ArrayList<>();

            updateRow(row, ENTRY_ACCESSION,      entry.getUniqueName());
            updateRow(row, TERM_ACCESSION,       annotation.getCvTermAccessionCode());
            updateRow(row, TERM_NAME,            annotation.getCvTermName());
            updateRow(row, ANNOTATION_QUALITY,   annotation.getQualityQualifier());
            updateRow(row, ECO_ACCESSION,        evidence.getEvidenceCodeAC());
            updateRow(row, ECO_NAME,             evidence.getEvidenceCodeName());
            updateRow(row, EVIDENCE_ASSIGNED_BY, evidence.getAssignedBy());
            updateRow(row, EVIDENCE_QUALITY,     evidence.getQualityQualifier());
            updateRow(row, EXPRESSION_LEVEL,     evidence.getExpressionLevel());

            ExperimentalContext ec = entry.getExperimentalContext(evidence.getExperimentalContextId())
                    .orElseThrow(() -> new NextProtException("missing experimental context for "+evidence.getEvidenceCodeAC()));

            updateRow(row, STAGE_ACCESSION,      ec.getDevelopmentalStageAC());
            updateRow(row, STAGE_NAME,          (ec.getDevelopmentalStage() != null) ? ec.getDevelopmentalStage().getName() : "null");
            updateRow(row, CELL_LINE_ACCESSION,  ec.getCellLineAC());
            updateRow(row, CELL_LINE_NAME,      (ec.getCellLine() != null) ? ec.getCellLine().getName() : "null");
            updateRow(row, DISEASE_ACCESSION,    ec.getDiseaseAC());
            updateRow(row, DISEASE_NAME,        (ec.getDisease() != null) ? ec.getDisease().getName() : "null");
            updateRow(row, ORGANELLE_ACCESSION,  ec.getOrganelleAC());
            updateRow(row, ORGANELLE_NAME,      (ec.getOrganelle() != null) ? ec.getOrganelle().getName() : "null");

            rows.add(new Row(row));
        }
    }

    private void updateRow(List<String> row, Header header, String value) {

        if (!exportHeaders().contains(header)) {
            return;
        }

        if (!headers.contains(header)) {
            throw new NextProtException("unknown header "+header);
        }

        if (headers.get(row.size()) != header) {
            throw new NextProtException("incorrect header "+header + " at position "+row.size()+ ": expected "+headers.get(row.size()));
        }

        row.add(value);
    }

    /**
     * Generic builder with common and custom fields to export
     */
    static class Builder {

        private final List<Header> headers = new ArrayList<>();

        Builder() {
            headers.addAll(Arrays.asList(
                    ENTRY_ACCESSION,
                    TERM_ACCESSION,
                    TERM_NAME,
                    ANNOTATION_QUALITY,
                    ECO_ACCESSION,
                    ECO_NAME,
                    EVIDENCE_ASSIGNED_BY,
                    EVIDENCE_QUALITY,
                    EXPRESSION_LEVEL
            ));
        }

        Builder addColumn(Header... headers) {

            for (Header header : headers) {
                if (!this.headers.contains(header)) {
                    this.headers.add(header);
                }
            }

            return this;
        }

        EntryPartExporterImpl build() {

            return new EntryPartExporterImpl(this);
        }
    }
}
