package com.kalhan.email_service.kafka.model;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"),
    PASSWORD_RESET("password_reset");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
