package com.dynamicwebservice.repository;

import com.dynamicwebservice.entity.MockResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockResponseRepository extends JpaRepository<MockResponseEntity, String> {
    List<MockResponseEntity> findByCondition(String condition);
}
