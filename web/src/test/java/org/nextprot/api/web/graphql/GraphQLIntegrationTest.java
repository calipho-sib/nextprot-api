package org.nextprot.api.web.graphql;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class GraphQLIntegrationTest extends WebIntegrationBaseTest {
	
  @Test
  public void shouldRunAGraphqlQuery() throws Exception {

      ResultActions result = this.mockMvc.perform(get("/graphql").accept(MediaType.APPLICATION_JSON));
      System.err.println(result.andReturn().getResponse().getContentAsString());

  }

}

