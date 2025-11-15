package com.arka.MSAuthentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String password;
    private String role;
    private String email;
    private Boolean isDeleted = false;
    private String direction;
    private String phone;

}
