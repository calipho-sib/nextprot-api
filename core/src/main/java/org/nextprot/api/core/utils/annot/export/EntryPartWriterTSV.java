package org.nextprot.api.core.utils.annot.export;

import org.nextprot.api.core.domain.Entry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Writes entry parts in tabs-separated-values
 * Delegate the type of infos to export to {@code EntryPartExporter}
 */
public class EntryPartWriterTSV extends EntryPartWriter<ByteArrayOutputStream> {

    private final EntryPartExporter exporter;

    public EntryPartWriterTSV(EntryPartExporter exporter) {

        super();
        this.exporter = exporter;
    }

    @Override
    protected ByteArrayOutputStream newOutputStream() {

        return new ByteArrayOutputStream();
    }

    @Override
    public void writeHeader(ByteArrayOutputStream outputStream) throws IOException {

        outputStream.write(exporter.exportHeaders().stream()
                .map(Enum::name)
                .collect(Collectors.joining("\t")).getBytes());
        outputStream.write("\n".getBytes());
    }

    @Override
    public void writeRows(Entry entry, ByteArrayOutputStream outputStream) throws IOException {

        outputStream.write(exporter.exportRows(entry).stream()
                .map(row -> row.stream().collect(Collectors.joining("\t")))
                .collect(Collectors.joining("\n")).getBytes());
    }

    public String getOutputString() throws UnsupportedEncodingException {

        return getOutputStream().toString(StandardCharsets.UTF_8.name());
    }
}
