package org.ilot.lib.cassandra.connector;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

public interface CassandraConnector {
    ResultSet execute(Statement statement);
    PreparedStatement createPrepatedStatement(String statement);
    Session getSession();
}