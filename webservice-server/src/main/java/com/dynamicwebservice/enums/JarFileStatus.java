package com.dynamicwebservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JarFileStatus {
    ACTIVE("A"),
    INACTIVE("I"),
    DELETED("D");

    private String status;

    public String getStatus() {
        return status;
    }

    public static JarFileStatus fromStatus(String status) {
        for (JarFileStatus jarFileStatus : JarFileStatus.values()) {
            if (jarFileStatus.getStatus().equals(status)) {
                return jarFileStatus;
            }
        }
        throw new IllegalArgumentException("Unknown enum status " + status);
    }
}
