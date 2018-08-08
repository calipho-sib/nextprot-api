package org.nextprot.api.commons.app;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.logging.Logger;

/**
 * Provide spring beans
 */
public class SpringBeanProvider {

    private static final Logger LOGGER = Logger.getLogger(SpringBeanProvider.class.getName());

    private final ClassPathXmlApplicationContext ctx;

    public SpringBeanProvider() {

        ctx = new ClassPathXmlApplicationContext(getXmlConfigResourceLocations());
    }

    /**
     * @return array of Spring resource locations
     */
    private String[] getXmlConfigResourceLocations() {
        return new String[] {
                "classpath:spring/commons-context.xml",
                "classpath:spring/core-context.xml"
        };
    }

    public <T> T getBean(Class<T> requiredType) {
        return ctx.getBean(requiredType);
    }
}
