package com.example.workflow.entities.request;

import com.example.workflow.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String password;
    private Role role;
    private String email;
}
