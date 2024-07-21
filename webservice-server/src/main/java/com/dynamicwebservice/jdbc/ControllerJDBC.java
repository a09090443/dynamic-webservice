package com.dynamicwebservice.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ControllerJDBC extends BaseJDBCAdapter {

    public static final String SQL_SELECT_CONTROLLER_RELATED_JAR_FILE = "SQL_SELECT_CONTROLLER_RELATED_JAR_FILE";

}
