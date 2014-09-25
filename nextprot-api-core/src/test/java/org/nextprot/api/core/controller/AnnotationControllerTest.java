package org.nextprot.api.core.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


public class AnnotationControllerTest extends MVCBaseIntegrationTest {
 
  @Autowired
  private WebApplicationContext wac;
 
  private MockMvc mockMvc;
 
  @Before
  public void setup() {
    this.mockMvc = webAppContextSetup(this.wac).build();
  }
 
  
  @Test
  @Ignore
  public void getAnnotations() throws Exception {
    this.mockMvc.perform(get("/entry/NX_P01903/annotation.xml"))
        .andExpect(status().isOk())
        .andExpect(xpath("/annotation-list").exists())
    	.andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation//@qualityQualifier").string("GOLD"))
    	.andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation/variant//@original").string("V"))
    	.andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation/evidence-list").exists())
    	.andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation/evidence-list//@isNegative").string("false"))
    	.andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation/evidence-list//@qualifierType").string("UNKNOWN"))
    	.andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation/evidence-list//@resourceAssocType").string("evidence"));
    
  }
  
  @Test
  @Ignore
  public void shouldGetAVariant() throws Exception {
    this.mockMvc.perform(get("/entry/NX_A2RRP1/annotation.xml"))
        .andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation/variant//@original").string("I")) //should exist a variant
        .andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation/variant//@variation").string("E")); //should exist a variant
  }

  @Test
  public void shouldNotGetAVariant() throws Exception {
    this.mockMvc.perform(get("/entry/NX_P01308/annotation.xml"))
        .andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation[0]/variant//@original").string("")) //Should not exist any variant
        .andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation[0]/variant//@variation").string("")); //Should not exist any variant
  }

  @Test
  public void shouldFixIssue11() throws Exception {
    this.mockMvc.perform(get("/entry/NX_P48730/annotation.xml"))
        .andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation[0]/variant//@original").string("")) //Should not exist any variant
        .andExpect(xpath("/annotation-list/annotation-to-be-renamed/annotation[0]/variant//@variation").string("")); //Should not exist any variant
  }
  
}

