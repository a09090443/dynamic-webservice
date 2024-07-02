package com.dynamicwebservice.repository;

import com.dynamicwebservice.entity.MockResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockResponseRepository extends JpaRepository<MockResponseEntity, String> {
    MockResponseEntity findByPublishUrlAndMethodAndConditionAndIsActive(String publishUrl, String method, String condition, Boolean isActive);
    List<MockResponseEntity> findByPublishUrl(String publishUrl);
}
