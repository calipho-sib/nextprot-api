package org.nextprot.api.web.dbunit.base.mvc;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"dev","cache"})
public abstract class WebIntegrationBaseTest extends WebUnitBaseTest {
}
