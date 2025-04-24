package com.example.workflow.util;


import com.example.workflow.entities.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class RolesConverter implements AttributeConverter<Set<Role>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return "{}";
        }

        String joined = roles.stream()
                .map(role -> String.valueOf(role.getCode()))
                .sorted()
                .collect(Collectors.joining(","));
        return "{" + joined + "}";
    }

    @Override
    public Set<Role> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || dbData.equals("{}")) {
            return new HashSet<>();
        }

        String trimmed = dbData.replaceAll("[{}]", "");
        if (trimmed.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(trimmed.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .map(Role::fromCode)
                .collect(Collectors.toSet());
    }
}
