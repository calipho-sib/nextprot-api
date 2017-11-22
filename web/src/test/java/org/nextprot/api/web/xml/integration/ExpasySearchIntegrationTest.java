package org.nextprot.api.web.xml.integration;
import org.junit.Test;
<<<<<<< HEAD
import org.mockito.internal.matchers.Contains;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.internal.matchers.LessThan;
import org.mockito.internal.matchers.StartsWith;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.test.web.servlet.ResultActions;
=======
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
>>>>>>> hotfix-2.10.2

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;


/**
 * The specifications of the expasy search interface can be found in here:
 * http://wiki.isb-sib.ch/web-team/Sib-resource_query-interface
 *
 */
public class ExpasySearchIntegrationTest extends WebIntegrationBaseTest {


<<<<<<< HEAD
  @Test
  public void shouldReployToExpasySearchWithXML() throws Exception {
	  this.mockMvc.perform(get("/expasy-search.xml?query=insulin&type=whatever"))
        .andExpect(status().isOk())
	  	.andExpect(xpath("/ExpasyResult/count").nodeCount(1))
        .andExpect(xpath("/ExpasyResult/url").string("https://www.nextprot.org/proteins/search?quality=gold-and-silver&query=insulin"));
  }
  
=======
    @Test
    public void shouldReployToExpasySearchWithXML() throws Exception {
        this.mockMvc.perform(get("/expasy-search.xml?query=insulin&type=whatever"))
                .andExpect(status().isOk())
                .andExpect(xpath("/ExpasyResult/count").nodeCount(1))
                .andExpect(xpath("/ExpasyResult/url").string("https://www.nextprot.org/proteins/search?quality=gold-and-silver&query=insulin"));
    }

>>>>>>> hotfix-2.10.2

}

