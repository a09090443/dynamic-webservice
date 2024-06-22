package com.dynamicwebservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MOCK_RESPONSE")
public class MockResponseEntity {

        @Column(name = "PUBLISH_URL", nullable = false)
        @Id
        private String publishUrl;

        @Column(name = "METHOD", nullable = false)
        private String method;

        @Column(name = "CONDITION", nullable = false)
        private String condition;

        @Column(name = "RESPONSE_CONTENT", nullable = false)
        private String responseContent;

        @Column(name = "IS_ACTIVE", nullable = false)
        private Boolean isActive;
}
