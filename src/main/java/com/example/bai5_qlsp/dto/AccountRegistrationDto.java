package com.example.bai5_qlsp.dto;

import lombok.Data;

@Data
public class AccountRegistrationDto {
    private String loginName;
    private String password;
    private String confirmPassword;
}
