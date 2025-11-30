package br.com.frotasyncro.domain.authentication.enums;

public enum UserStatus {
    ACTIVE, INACTIVE, CONFIGURATION;

    public boolean isActive() {
        return this == ACTIVE || this == CONFIGURATION;
    }

}
