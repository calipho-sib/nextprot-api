package org.nextprot.api.web.graphql;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GraphQLIntegrationTest extends WebIntegrationBaseTest {
	
  @Test
  public void shouldRunAGraphqlQuery() throws Exception {

      String query = "{entry (accession:\"P06213\") { isoforms { sequence }";

      ResultActions result = this.mockMvc.perform(post("/graphql").accept(MediaType.APPLICATION_JSON).content(query));
      result.andExpect(status().isOk());
      System.err.println(result.andReturn().getResponse().getContentAsString());

  }

}

