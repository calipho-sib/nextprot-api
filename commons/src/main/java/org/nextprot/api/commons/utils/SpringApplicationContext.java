package org.nextprot.api.commons.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;

/**
 * Wrapper to always return a reference to the Spring Application Context from
 * within non-Spring enabled beans. Unlike Spring MVC's WebApplicationContextUtils
 * we do not need a reference to the Servlet context for this. All we need is
 * for this bean to be initialized during application startup.
 *
 * The original code has been improved by adding the convenient method getAllBeans()
 * and applying generics to the methods.
 *
 * URL source: http://sujitpal.blogspot.com/2007/03/accessing-spring-beans-from-legacy-code.html
 */
public class SpringApplicationContext implements ApplicationContextAware {

	private static final Logger LOGGER = Logger.getLogger(SpringApplicationContext.class);
	private static ApplicationContext CONTEXT;

	/**
	 * This method is called from within the ApplicationContext once it is
	 * done starting up, it will stick a reference to itself into this bean.
	 * @param context a reference to the ApplicationContext.
	 */
	public void setApplicationContext(ApplicationContext context) throws BeansException {

		CONTEXT = context;
		LOGGER.info("Give access to application context from class SpringApplicationContext");
	}

	/**
	 * Give access to a bean of required type from the Spring application context.
	 * If the bean does not exist, then a Runtime error will be thrown.
	 * @param requiredType the type of the bean to get.
	 * @return an T-typed bean.
	 */
	public static <T> T getBeanOfType(Class<T> requiredType) {

		return CONTEXT.getBean(requiredType);
	}

	/**
	 * Give access to all the beans of the same required type from the Spring application context.
	 * @param requiredType the type of the bean to get.
	 * @return a collection of T-typed beans.
	 */
	public static <T> Collection<T> getAllBeansOfType(Class<T> requiredType) {

		return CONTEXT.getBeansOfType(requiredType).values();
	}
}