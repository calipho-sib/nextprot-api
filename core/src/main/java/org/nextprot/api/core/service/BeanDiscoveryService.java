package org.nextprot.api.core.service;

import java.util.Collection;

public interface BeanDiscoveryService {

    <T> T getBean(Class<T> beanClass);

	<T> Collection<T> getAllBeans(Class<T> beanClass);
}
