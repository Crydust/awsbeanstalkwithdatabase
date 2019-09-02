package be.crydust.spike.business;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SingleConnectionDataSource extends DelegatingDataSource {

    private volatile Connection connection = null;

    public SingleConnectionDataSource(DataSource ds) {
        super(ds);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    connection = super.getConnection();
                }
            }
        }
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    connection = super.getConnection(username, password);
                }
            }
        }
        return connection;
    }
}
