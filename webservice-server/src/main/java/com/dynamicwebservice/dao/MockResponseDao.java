package com.dynamicwebservice.dao;

import com.dynamicwebservice.jdbc.MockResponseJDBC;
import com.zipe.enums.ResourceEnum;
import com.zipe.jdbc.criteria.Conditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MockResponseDao {

    private final MockResponseJDBC mockResponseJDBC;

    public MockResponseDao(MockResponseJDBC mockResponseJDBC) {
        this.mockResponseJDBC = mockResponseJDBC;
    }

    public String findByPrimaryKey(String publishUrl, String method, String condition) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(MockResponseJDBC.SQL_SELECT_RESPONSE_CONTENT);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("publishUrl", publishUrl);
        paramMap.put("method", method);
        paramMap.put("condition", condition);

        try {
            return mockResponseJDBC.queryForObject(resource, paramMap, String.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("publishUrl:{}", publishUrl);
            log.error("method:{}", method);
            log.error("condition:{}", condition);
            log.error("IncorrectResultSizeDataAccessException:{}", e.getMessage(), e);
            return "";
        }
    }
}
