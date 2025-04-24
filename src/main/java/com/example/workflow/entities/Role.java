package com.example.workflow.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@Getter
public enum Role /*implements GrantedAuthority*/ {
    ADMIN(1, "ADMIN"),
    EDUCATOR(2, "EDUCATOR"),
    USER(3, "USER");

    private final int code;
    private final String value;

//    @Override
//    public String getAuthority() {
//        return value;
//    }

    public static Role fromCode(int code) {
        for (Role role : Role.values()) {
            if (role.getCode() == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown Role code: " + code);
    }
}
