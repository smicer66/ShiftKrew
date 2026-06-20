package com.syncstate.go.cardinal.inside.ShiftKrew.enums;

public enum UserRole {
    SYSTEM("SYSTEM"),
    PRIVATE_CUSTOMER("PRIVATE_CUSTOMER"),
    ORGANISATION_CUSTOMER("ORGANISATION_CUSTOMER");



    public final String value;

    private UserRole(String value) {
        this.value = value;
    }

    public UserRole valueOfLabel(String label) {
        for (UserRole e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
