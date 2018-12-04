package org.nextprot.api.web.dbunit.base.mvc;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.junit.Before;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Base class used to test mock controllers on top of dbunit test
 * Nice article: http://coderphil.wordpress.com/2012/04/23/database-testing-using-dbunit-spring-and-annotations/
 * @author dteixeira
 */

@TransactionConfiguration(defaultRollback = true)
@Transactional
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
@ContextConfiguration("classpath:META-INF/spring/web-context.xml")
public abstract class WebUnitBaseTest extends AbstractUnitBaseTest {

	private static final Logger LOGGER = Logger.getLogger(WebUnitBaseTest.class);

	@Autowired
	private VelocityConfig velocityConfig;
	
	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).build();

		LOGGER.info("setup mockmvc with ApplicationContext "+wac.getDisplayName());
	}
	
	
	protected String getVelocityOutput(Entry entry) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer writer = new PrintWriter(out);

		NXVelocityContext context = new NXVelocityContext(entry);
		Template template = velocityConfig.getVelocityEngine().getTemplate("entry.xml.vm");
		template.merge(context, writer);
		writer.flush();
		return out.toString();
	}


}
