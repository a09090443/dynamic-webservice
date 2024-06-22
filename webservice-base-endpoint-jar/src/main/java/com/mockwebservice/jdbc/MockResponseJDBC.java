package com.mockwebservice.jdbc;

import com.zipe.jdbc.BaseJDBC;
import org.springframework.stereotype.Repository;

@Repository
public class MockResponseJDBC extends BaseJDBC {

    public static final String SQL_SELECT_RESPONSE_CONTENT = "SQL_SELECT_RESPONSE_CONTENT";

}
