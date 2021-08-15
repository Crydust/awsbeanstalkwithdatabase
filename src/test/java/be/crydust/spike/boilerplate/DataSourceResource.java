package be.crydust.spike.boilerplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.asserts.ParameterByIndexHolder;
import net.ttddyy.dsproxy.asserts.ParameterKeyValue;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;
import net.ttddyy.dsproxy.asserts.QueryHolder;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

public class DataSourceResource implements BeforeEachCallback, AfterEachCallback {

    private final String connectionUrl;
    private final String user;
    private final String password;
    private final boolean allowMultipleSimultaneousConnections;
    private ProxyTestDataSource ds;

    public DataSourceResource() {
        this(false);
    }

    public DataSourceResource(boolean allowMultipleSimultaneousConnections) {
        this("jdbc:h2:mem:test-" + UUID.randomUUID().toString(), "sa", "", allowMultipleSimultaneousConnections);
    }

    private DataSourceResource(String connectionUrl, String user, String password, boolean allowMultipleSimultaneousConnections) {
        this.connectionUrl = connectionUrl;
        this.user = user;
        this.password = password;
        this.allowMultipleSimultaneousConnections = allowMultipleSimultaneousConnections;
    }

    public ProxyTestDataSource get() {
        return ds;
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        final DataSource realDataSource = allowMultipleSimultaneousConnections
                ? createHikariDataSource()
                : createSimpleDataSource();
        Flyway.configure().dataSource(realDataSource).load().migrate();
        this.ds = new ProxyTestDataSource(realDataSource);
    }

    private SimpleDataSource createSimpleDataSource() {
        return new SimpleDataSource(connectionUrl, user, password);
    }

    private HikariDataSource createHikariDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setMinimumIdle(0);
        config.setMaximumPoolSize(2);
        config.setUsername(user);
        config.setPassword(password);
        config.setJdbcUrl(connectionUrl);
        return new HikariDataSource(config);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (ds != null) {
            try {
                ds.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void logSqlStatements() {
        System.out.println("- sql statements ----------------------------------------------------------------");
        final List<QueryExecution> queryExecutions = ds.getQueryExecutions();
        for (int i = 0; i < queryExecutions.size(); i++) {
            final QueryExecution queryExecution = queryExecutions.get(i);
            final String sql;
            if (queryExecution instanceof QueryHolder) {
                sql = ((QueryHolder) queryExecution).getQuery()
                        .replaceAll("\\s+", " ")
                        .trim();
            } else {
                sql = "?";
            }
            System.out.printf("%d. %s%n", (i), sql);

            if (queryExecution instanceof ParameterByIndexHolder) {
                final SortedSet<ParameterKeyValue> parameters = ((ParameterByIndexHolder) queryExecution).getAllParameters();
                for (ParameterKeyValue parameter : parameters) {
                    System.out.printf("%d.%d. %s%n", (i + 1), parameter.getKey().getIndex(), parameter.getDisplayValue());
                }
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
    }
}
