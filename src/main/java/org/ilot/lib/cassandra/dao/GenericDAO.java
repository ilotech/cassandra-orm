package org.ilot.lib.cassandra.dao;


import com.datastax.driver.core.Statement;

public interface GenericDAO<T> {
    void write(T object) throws IllegalAccessException;
    T find(Statement statement) throws ResultSetNotFoundException;
    void delete(Statement statement);
}