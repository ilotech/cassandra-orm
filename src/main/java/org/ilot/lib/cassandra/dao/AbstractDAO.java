package org.ilot.lib.cassandra.dao;

import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import org.ilot.lib.cassandra.connector.CassandraConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractDAO<T> implements GenericDAO<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final CassandraConnector cassandraConnector;

    protected final String tableName;
    protected final String createTableStatement;
    protected final String keyspaceName;

    public AbstractDAO(CassandraConnector cassandraConnector) {
        this.cassandraConnector = cassandraConnector;
        this.tableName = getTableName();
        this.createTableStatement = getCreateTableStatement();
        this.keyspaceName = cassandraConnector.getSession().getLoggedKeyspace();

        createTable();
    }

    protected abstract String getTableName();
    protected abstract String getCreateTableStatement();

    private void createTable() {
        Statement simpleStatement = new SimpleStatement(createTableStatement);
        cassandraConnector.execute(simpleStatement);
    }

    public void write(Statement statement) {
        cassandraConnector.execute(statement);
    }

    public void delete(Statement statement) {
        cassandraConnector.execute(statement);
    }
}