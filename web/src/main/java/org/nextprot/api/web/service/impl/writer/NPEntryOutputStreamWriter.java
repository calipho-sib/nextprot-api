package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.web.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

import java.io.OutputStream;

/**
 * A base class for output stream exporter
 *
 * @author fnikitin
 */
public abstract class NPEntryOutputStreamWriter extends NPEntryWriter<OutputStream> {

    protected final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    protected EntryBuilderService entryBuilderService;

    public NPEntryOutputStreamWriter(OutputStream os) {

        super(os);

        this.entryBuilderService = applicationContext.getBean(EntryBuilderService.class);
    }
}
