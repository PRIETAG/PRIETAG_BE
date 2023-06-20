package com.tag.prietag.model;

public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    RoleEnum(String value) {  // 스트링 타입으로 변환하려고
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RoleEnum fromString(String value) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.value.equalsIgnoreCase(value)) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}
