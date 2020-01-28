package org.ilot.lib.cassandra.dao;


import com.datastax.driver.core.ResultSet;

import java.util.Map;

public interface RowMapper<T> {
    T toObject(ResultSet resultSet);
    Map<String, Object> toRows(T object);
}