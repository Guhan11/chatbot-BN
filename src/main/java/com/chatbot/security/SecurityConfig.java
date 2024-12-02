package com.chatbot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.chatbot.util.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	// Bean to define PasswordEncoder for password encryption
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

// Security configuration for HTTP requests
@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
				.disable().formLogin().disable().httpBasic().disable().exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint).and().authorizeRequests()
				.antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/swagger-ui.html",
						"/swagger-ui/**", "/webjars/**", "/swagger-ui/index.html", "/api/user/getProfileImage",
						"/api/user/uploadProfileImage")
				.permitAll()
				.antMatchers("/", "/error", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
						"/**/*.html", "/**/*.css", "/**/*.js")
				.permitAll().antMatchers("/api/auth/**").permitAll().antMatchers("/api/user/**", "/api/chatBot")
				.authenticated().antMatchers("/api/**").hasAnyRole("USER", "GUEST_USER").anyRequest().authenticated();

		// Add JWT Authentication Filter before UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
