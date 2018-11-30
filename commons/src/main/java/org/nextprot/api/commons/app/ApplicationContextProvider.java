package org.nextprot.api.commons.app;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Provides ApplicationContext to any class needing to interact with context.
 *
 * Created by fnikitin on 29/04/15.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static final Logger LOGGER = Logger.getLogger(ApplicationContextProvider.class);

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {

        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        LOGGER.info("Give access to application context "+ctx.getDisplayName() + " from class ApplicationContextProvider");

        applicationContext = ctx;
    }
}