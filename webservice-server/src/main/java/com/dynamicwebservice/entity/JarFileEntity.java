package com.dynamicwebservice.entity;

import com.dynamicwebservice.converter.JarFileStatusConverter;
import com.dynamicwebservice.enums.JarFileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "JAR_FILE")
public class JarFileEntity extends BaseEntity {

    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "STATUS", nullable = false)
    @Convert(converter = JarFileStatusConverter.class)
    private JarFileStatus status;
}
