package org.nextprot.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	  auth.authenticationProvider(new ThreeScaleAuthenticationProvider());
//	  inMemoryAuthentication().withUser("dani").password("123").roles("USER");
//	  auth.inMemoryAuthentication().withUser("admin").password("123").roles("ADMIN");
	}
 
	//check this out http://stackoverflow.com/questions/24492835/spring-security-oauth-stackoverflowexception
	//http://spring.io/blog/2013/07/03/spring-security-java-config-preview-web-security/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
 
	  http.authorizeRequests()
		.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
		.antMatchers("/entry/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
		.and().formLogin();
 
	}
}