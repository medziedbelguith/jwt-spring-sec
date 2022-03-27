package org.sid.sec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
	// PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	//PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	//@Bean
	//public BCryptPasswordEncoder passwordEncoder() {
	//    return new BCryptPasswordEncoder();
//	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth)throws Exception{
	/*	auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder.encode("1234")).roles("ADMIN","USER")
		.and()
		.withUser("user").password(passwordEncoder.encode("1234")).roles("USER");*/
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
   @Override
   protected void configure(HttpSecurity http)throws Exception{
	http.csrf().disable();
	//http.formLogin();
	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	http.authorizeRequests().antMatchers("/login/**","/register/**").permitAll();
	http.authorizeRequests().antMatchers(HttpMethod.POST,"/tasks/**").access("hasRole('ADMIN')");
	http.authorizeRequests().anyRequest().authenticated();
	http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
	http.addFilterBefore(new JWTAuthorizationFilter(),UsernamePasswordAuthenticationFilter.class);
	}
}
