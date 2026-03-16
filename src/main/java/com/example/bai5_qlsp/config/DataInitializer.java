package com.example.bai5_qlsp.config;

import com.example.bai5_qlsp.entity.Account;
import com.example.bai5_qlsp.entity.Role;
import com.example.bai5_qlsp.repository.AccountRepository;
import com.example.bai5_qlsp.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner initData(
			RoleRepository roleRepository,
			AccountRepository accountRepository,
			PasswordEncoder passwordEncoder
	) {
		return args -> {
			Role adminRole = roleRepository.findByName("ROLE_ADMIN")
					.orElseGet(() -> {
						Role role = new Role();
						role.setName("ROLE_ADMIN");
						return roleRepository.save(role);
					});

			Role userRole = roleRepository.findByName("ROLE_USER")
					.orElseGet(() -> {
						Role role = new Role();
						role.setName("ROLE_USER");
						return roleRepository.save(role);
					});

			accountRepository.findByLoginName("admin")
					.orElseGet(() -> {
						Account admin = new Account();
						admin.setLoginName("admin");
						admin.setPassword(passwordEncoder.encode("admin123"));
						admin.setRoles(Set.of(adminRole));
						return accountRepository.save(admin);
					});

			accountRepository.findByLoginName("user1")
					.orElseGet(() -> {
						Account user = new Account();
						user.setLoginName("user1");
						user.setPassword(passwordEncoder.encode("123456"));
						user.setRoles(Set.of(userRole));
						return accountRepository.save(user);
					});
		};
	}
}

