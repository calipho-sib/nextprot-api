package org.nextprot.api.web.service.impl.writer;

import org.nextprot.api.commons.app.ApplicationContextProvider;
import org.nextprot.api.core.service.EntryBuilderService;
import org.springframework.context.ApplicationContext;

import java.io.OutputStream;

/**
 * A base class for output stream writer
 *
 * @author fnikitin
 */
public abstract class EntryOutputStreamWriter extends EntryStreamWriter<OutputStream> {

    protected final ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    protected EntryBuilderService entryBuilderService;

    public EntryOutputStreamWriter(OutputStream os) {

        super(os);

        this.entryBuilderService = applicationContext.getBean(EntryBuilderService.class);
    }
}
