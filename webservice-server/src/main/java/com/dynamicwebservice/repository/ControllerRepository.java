package com.dynamicwebservice.repository;

import com.dynamicwebservice.entity.ControllerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControllerRepository extends JpaRepository<ControllerEntity, String> {
    ControllerEntity findByUuId(String id);

    List<ControllerEntity> findAllByIsActive(Boolean isActive);
}
