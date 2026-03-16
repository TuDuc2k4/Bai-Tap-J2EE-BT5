package com.example.bai5_qlsp.config;

import java.io.IOException;

import com.example.bai5_qlsp.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private AccountService accountService;

	@Bean
	SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http)
			throws Exception {
		http
				.userDetailsService(accountService)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/error").permitAll()
						.requestMatchers("/shop/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/products/**", "/categories/**").hasRole("ADMIN")
						.requestMatchers("/home").hasAnyRole("USER", "ADMIN")
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.successHandler(this::onAuthenticationSuccess)
						.permitAll()
				)
				.logout(withDefaults());

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) throws IOException, ServletException {
		var authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		if (authorities.contains("ROLE_ADMIN")) {
			response.sendRedirect("/products");
			return;
		}
		response.sendRedirect("/home");
	}
}

