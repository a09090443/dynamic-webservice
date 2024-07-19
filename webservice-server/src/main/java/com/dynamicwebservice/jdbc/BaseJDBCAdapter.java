package com.dynamicwebservice.jdbc;

import com.zipe.jdbc.BaseJDBC;
import com.zipe.jdbc.criteria.Conditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BaseJDBCAdapter extends BaseJDBC {

    public <T> List<T> queryForList(String sql, Conditions conditions, Map<String, Object> params, Class<T> clazz) {

        String sqlText = null;

        if (conditions != null) {
            sqlText = conditions.done(sql);
        }

        List<T> rtnLs = this.support.getNamedParameterJdbcTemplate().query(sqlText, params, new BeanPropertyRowMapper(clazz));
        return CollectionUtils.isEmpty(rtnLs) ? Collections.emptyList() : rtnLs;
    }

    public int update(String sql, Map<String, Object> sqlParams) {

        return this.support.getNamedParameterJdbcTemplate().update(sql, sqlParams);
    }

    public <T> T queryForObject(String sql, Map<String, Object> params, Class<T> clazz) {
        T dataLs = this.support.getNamedParameterJdbcTemplate().queryForObject(sql, params, clazz);
        return ObjectUtils.isEmpty(dataLs) ? null : dataLs;
    }
}
