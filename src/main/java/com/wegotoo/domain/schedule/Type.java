package com.wegotoo.domain.schedule;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Type {

    LOCATION("TYPE_LOCATION", "장소"),
    MEMO("TYPE_MEMO", "메모");

    private final String key;
    private final String label;

    public static Type fromKey(String key) {
        for (Type type : Type.values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with key " + key);
    }

}
