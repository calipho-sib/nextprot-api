package org.nextprot.api.web.dbunit.base.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles({"dev","cache"})
public abstract class WebIntegrationBaseTest extends WebUnitBaseTest {

    @Autowired
    protected WebApplicationContext wac;
}
