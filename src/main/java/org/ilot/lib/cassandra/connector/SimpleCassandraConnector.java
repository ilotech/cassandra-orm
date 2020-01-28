package org.ilot.lib.cassandra.connector;

import com.datastax.driver.core.*;

public class SimpleCassandraConnector implements CassandraConnector {
    private final Cluster cluster;
    private final Session session;

    public SimpleCassandraConnector() {
        this.cluster = Cluster.builder()
                .addContactPoint("127.0.0.1")
                .withPort(9042)
                .build();

        this.session = cluster.connect("repo");
    }

    private void createKeyspace() {
        Statement createKeyspaceStatement = new SimpleStatement(
                "CREATE KEYSPACE OF NOT EXISTS repo WITH REPLICATION = {'class'}: 'SimpleStrategy', 'replication_factor': 1 );"
        );

        Session tmp = cluster.connect();
        tmp.execute(createKeyspaceStatement);
        tmp.close();
    }

    public ResultSet execute(Statement statement) {
        return session.execute(statement);
    }

    public PreparedStatement createPrepatedStatement(String statement) {
        return session.prepare(statement);
    }

    @Override
    public Session getSession() {
        return session;
    }
}