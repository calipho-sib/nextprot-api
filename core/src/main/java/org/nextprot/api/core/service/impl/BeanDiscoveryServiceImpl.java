package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.service.BeanDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
public class BeanDiscoveryServiceImpl implements BeanDiscoveryService {

    @Autowired
    private ApplicationContext ctx = null;

    public <T> T getBean(Class<T> beanClass) {

        return ctx.getBean(beanClass);
    }

	@Override
	public <T> Collection<T> getAllBeans(Class<T> beanClass) {

		return ctx.getBeansOfType(beanClass).values();
	}
}
