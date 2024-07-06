package com.dynamicwebservice.repository;

import com.dynamicwebservice.dao.MockResponseDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tw.com.webcomm.base.TestBase;

public class ResponseRepositoryTest extends TestBase {

    @Autowired
    public MockResponseDao mockResponseDao;

    @Test
    void testFindByCondition() {
        String content = mockResponseDao.findByPrimaryKey("company", "getCompany", "{\"employees\":null,\"name\":\"Gay\",\"taxId\":\"123456789\"}");
        System.out.println(content);
//        Assertions.assertEquals(mockResponseXml(), content);
    }

    private String requestJson() {
        return """
                {"employees":null,"name":"Gary","taxId":"123456789"}
                """;
    }

}
