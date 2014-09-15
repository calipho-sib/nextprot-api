package org.nextprot.api.user.controller;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.dbunit.MVCBaseSecurityIntegrationTest;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class UserApplicationControllerTest extends MVCBaseSecurityIntegrationTest{

	@Mock
    UserApplicationService applicationService;

    @InjectMocks
    UserApplicationController applicationController;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(applicationController).addFilters(this.springSecurityFilterChain).build();

    }
    
	@Test
	public void shouldCreateAUserApplication() throws Exception {
		
        when(applicationService.createUserApplication(isA(UserApplication.class))).thenReturn(new UserApplication());

		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS, Arrays.asList(new String[] {"USER_ROLE"}));
		
		this.mockMvc.perform(post("/user/applications").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"name\"}")).andExpect(status().isOk());

	}

	
	@Test
	public void shouldBEForbidderApplication() throws Exception {
		
		UserApplication ua =new UserApplication();
		ua.setOwner("zzzzz");
		
        when(applicationService.getUserApplication(isA(Long.class))).thenReturn(ua);

		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS, Arrays.asList(new String[] {"USER_ROLE"}));
		
		this.mockMvc.perform(get("/user/applications/1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());

	}
}
