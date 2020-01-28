package org.ilot.lib.cassandra.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.ilot.lib.cassandra.connector.CassandraConnector;

public abstract class AbstractRowMapperDAO<T> extends AbstractDAO<T> {
    private final RowMapper<T> rowMapper;

    public AbstractRowMapperDAO(CassandraConnector cassandraConnector, RowMapper<T> rowMapper) {
        super(cassandraConnector);
        this.rowMapper = rowMapper;
    }

    @Override
    public final void write(T object) {
        Insert statement = QueryBuilder.insertInto(keyspaceName, tableName);
        rowMapper.toRows(object).forEach(statement::value);
        cassandraConnector.execute(statement);
    }

    public final T find(Statement statement) throws ResultSetNotFoundException {
        ResultSet resultSet = cassandraConnector.execute(statement);
        if (resultSet == null) {
            String message = "ResultSet is null for statement: " + statement;
            logger.debug(message);
            throw new ResultSetNotFoundException(message);
        }
        return rowMapper.toObject(resultSet);
    }
}