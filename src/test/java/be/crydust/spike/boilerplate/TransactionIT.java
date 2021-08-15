package be.crydust.spike.boilerplate;

import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;

class TransactionIT {

    @RegisterExtension
    DataSourceResource dataSourceResource = new DataSourceResource();

    private ProxyTestDataSource ds;

    @BeforeEach
    public void setUp() {
        ds = dataSourceResource.get();
    }

    @AfterEach
    public void tearDown() {
        dataSourceResource.logSqlStatements();
    }

    @Test
    void name() {
        new Transaction(ds, ds -> {
            try (var con = ds.getConnection();
                 var ps = con.prepareStatement("select 'X' as \"dummy\"");
                 var rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("dummy = " + rs.getString(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).run();
    }

}