package com.example.bai5_qlsp.controller;

import com.example.bai5_qlsp.dto.AccountRegistrationDto;
import com.example.bai5_qlsp.entity.Account;
import com.example.bai5_qlsp.entity.Role;
import com.example.bai5_qlsp.repository.AccountRepository;
import com.example.bai5_qlsp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AccountRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") AccountRegistrationDto registrationDto, Model model) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "register";
        }

        if (accountRepository.findByLoginName(registrationDto.getLoginName()).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại.");
            return "register";
        }

        Account account = new Account();
        account.setLoginName(registrationDto.getLoginName());
        account.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });
        account.setRoles(Set.of(userRole));

        accountRepository.save(account);

        return "redirect:/login?success"; // Sẽ redirect về /login của Spring Security
    }
}
