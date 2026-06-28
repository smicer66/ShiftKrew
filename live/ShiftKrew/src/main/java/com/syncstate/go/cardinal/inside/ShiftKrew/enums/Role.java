package com.syncstate.go.cardinal.inside.ShiftKrew.enums;

public enum Role {
    SYSTEM("SYSTEM"),
    EMPLOYEE("Employee"),
    EMPLOYER("Employer");



    public final String value;

    private Role(String value) {
        this.value = value;
    }

    public Role valueOfLabel(String label) {
        for (Role e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
