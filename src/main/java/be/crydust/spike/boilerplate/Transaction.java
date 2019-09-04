package be.crydust.spike.boilerplate;

import be.crydust.spike.business.RepositoryException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class Transaction implements Runnable {
    private final DataSource ds;
    private final Consumer<DataSource> consumer;

    public Transaction(DataSource ds, Consumer<DataSource> consumer) {
        this.ds = ds;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        final SingleConnectionDataSource sds = new SingleConnectionDataSource(ds);
        try (final Connection con = sds.getConnection()) {
            con.setAutoCommit(false);
            try {
                consumer.accept(ds);
                con.commit();
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }
}
