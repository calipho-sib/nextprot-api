package org.nextprot.api.core.service;

public interface BeanService {

    <T> T getBean(Class<T> beanClass);
}
