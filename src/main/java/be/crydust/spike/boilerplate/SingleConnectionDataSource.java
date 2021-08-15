package be.crydust.spike.boilerplate;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

class SingleConnectionDataSource extends DelegatingDataSource implements Closeable {

    private volatile UncloseableConnection connection = null;

    SingleConnectionDataSource(DataSource ds) {
        super(ds);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    connection = new UncloseableConnection(super.getConnection());
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
                    connection = new UncloseableConnection(super.getConnection(username, password));
                }
            }
        }
        return connection;
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.reallyClose();
            } catch (SQLException ex) {
                throw new IOException(ex);
            }
        }
    }

}
