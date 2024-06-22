package com.dynamicwebservice.repository;

import com.dynamicwebservice.entity.JarFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JarFileRepository extends JpaRepository<JarFileEntity, String> {
}
