package org.nextprot.api.web;

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

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {

        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        applicationContext = ctx;
    }
}