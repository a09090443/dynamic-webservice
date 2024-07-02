package com.dynamicwebservice.entity;

import jakarta.persistence.Column;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseEntity {
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    public Timestamp createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    public Timestamp updatedAt;
}
