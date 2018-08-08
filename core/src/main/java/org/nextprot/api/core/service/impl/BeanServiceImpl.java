package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.service.BeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


@Service
public class BeanServiceImpl implements BeanService {

    @Autowired
    private ApplicationContext ctx = null;

    public <T> T getBean(Class<T> beanClass) {

        return ctx.getBean(beanClass);
    }
}
