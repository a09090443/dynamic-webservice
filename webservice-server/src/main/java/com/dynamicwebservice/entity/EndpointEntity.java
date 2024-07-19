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
@Table(name = "ENDPOINT")
public class EndpointEntity extends BaseEntity {

    @Column(name = "ID", nullable = false)
    private String uuId;

    @Column(name = "PUBLISH_URI", nullable = false)
    @Id
    private String publishUri;

    @Column(name = "BEAN_NAME", nullable = false)
    private String beanName;

    @Column(name = "JAR_FILE_ID", nullable = false)
    private String jarFileId;

    @Column(name = "CLASS_PATH", nullable = false)
    private String classPath;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive;
}
