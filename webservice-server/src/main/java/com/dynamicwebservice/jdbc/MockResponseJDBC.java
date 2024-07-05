package com.dynamicwebservice.jdbc;

import com.zipe.jdbc.BaseJDBC;
import org.springframework.stereotype.Repository;

@Repository
public class MockResponseJDBC extends BaseJDBC {

    public static final String SQL_SELECT_RESPONSE_CONTENT = "SQL_SELECT_RESPONSE_CONTENT";
    public static final String SQL_UPDATE_RESPONSE = "SQL_UPDATE_RESPONSE";
    public static final String SQL_UPDATE_PUBLISH_URL_FOR_RESPONSE = "SQL_UPDATE_PUBLISH_URL_FOR_RESPONSE";
    public static final String SQL_DEL_RESPONSE = "SQL_DEL_RESPONSE";

}
