package com.syncstate.go.cardinal.inside.ShiftKrew.enums;

public enum Permission {
    CREATE_NEW_ACQUIRER("Create New Acquirers");

    public final String value;

    private Permission(String value) {
        this.value = value;
    }

    public Permission valueOfLabel(String label) {
        for (Permission e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }

    public static String getValue(Permission p)
    {
        return p.value;
    }

}
