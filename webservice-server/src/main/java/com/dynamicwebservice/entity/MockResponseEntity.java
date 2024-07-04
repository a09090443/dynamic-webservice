package com.dynamicwebservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MOCK_RESPONSE")
public class MockResponseEntity extends BaseEntity{

        @Column(name = "ID", nullable = false)
        private String uuId;

        @EmbeddedId
        private MockResponseId id = new MockResponseId();

        @Column(name = "RESPONSE_CONTENT")
        private String responseContent;

        @Column(name = "IS_ACTIVE", nullable = false)
        private Boolean isActive;
}
