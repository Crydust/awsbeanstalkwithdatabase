package be.crydust.spike.boilerplate;

import java.sql.Connection;
import java.sql.SQLException;

class UncloseableConnection extends DelegatingConnection {

    public UncloseableConnection(Connection con) {
        super(con);
    }

    @Override
    public void close() {
        // NOOP
    }

    void reallyClose() throws SQLException {
        super.close();
    }
}
