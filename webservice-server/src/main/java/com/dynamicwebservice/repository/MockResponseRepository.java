package com.dynamicwebservice.repository;

import com.dynamicwebservice.entity.MockResponseEntity;
import com.dynamicwebservice.entity.MockResponseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockResponseRepository extends JpaRepository<MockResponseEntity, MockResponseId> {
    MockResponseEntity findByIdPublishUrlAndIdMethodAndIdConditionAndIsActive(String publishUrl, String method, String condition, Boolean isActive);
    List<MockResponseEntity> findByIdPublishUrl(String publishUrl);
    MockResponseEntity findByUuId(String id);
}
