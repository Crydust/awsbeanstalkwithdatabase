package be.crydust.spike.boilerplate;

import net.ttddyy.dsproxy.asserts.ParameterByIndexHolder;
import net.ttddyy.dsproxy.asserts.ParameterKeyValue;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;
import net.ttddyy.dsproxy.asserts.QueryHolder;
import org.apache.tomcat.jdbc.pool.PoolProperties;
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

    private final String driverClassName;
    private final String connectionUrl;
    private final String user;
    private final String password;
    private final boolean allowMultipleSimultaneousConnections;
    private ProxyTestDataSource ds;

    public DataSourceResource() {
        this(false);
    }

    public DataSourceResource(boolean allowMultipleSimultaneousConnections) {
        this("org.h2.Driver", "jdbc:h2:mem:test-" + UUID.randomUUID().toString(), "sa", "", allowMultipleSimultaneousConnections);
    }

    private DataSourceResource(String driverClassName, String connectionUrl, String user, String password, boolean allowMultipleSimultaneousConnections) {
        this.driverClassName = driverClassName;
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
                ? createTomcatJdbcDataSource()
                : createSimpleDataSource();
        Flyway.configure().dataSource(realDataSource).load().migrate();
        this.ds = new ProxyTestDataSource(realDataSource);
    }

    private DataSource createSimpleDataSource() {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new SimpleDataSource(connectionUrl, user, password);
    }

    private DataSource createTomcatJdbcDataSource() {
        PoolProperties p = new PoolProperties();
        p.setInitialSize(0);
        p.setMinIdle(0);
        p.setMaxIdle(2);
        p.setMaxActive(2);
        p.setUsername(user);
        p.setPassword(password);
        p.setDriverClassName(driverClassName);
        p.setUrl(connectionUrl);
        return new org.apache.tomcat.jdbc.pool.DataSource(p);
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
