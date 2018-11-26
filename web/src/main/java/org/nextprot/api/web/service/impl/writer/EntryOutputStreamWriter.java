package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.core.service.EntryBuilderService;

import java.io.OutputStream;

/**
 * A base class for output stream writer
 *
 * @author fnikitin
 */
public abstract class EntryOutputStreamWriter extends EntryStreamWriter<OutputStream> {

    protected EntryBuilderService entryBuilderService;

    public EntryOutputStreamWriter(OutputStream os) {

        super(os);

        this.entryBuilderService = SpringApplicationContext.getBeanOfType(EntryBuilderService.class);
    }
}
