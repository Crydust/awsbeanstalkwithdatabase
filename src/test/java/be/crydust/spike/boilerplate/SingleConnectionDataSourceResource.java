package be.crydust.spike.boilerplate;

import net.ttddyy.dsproxy.asserts.ParameterByIndexHolder;
import net.ttddyy.dsproxy.asserts.ParameterKeyValue;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;
import net.ttddyy.dsproxy.asserts.QueryHolder;
import org.flywaydb.core.Flyway;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;

public class SingleConnectionDataSourceResource extends ExternalResource {

    private static final Consumer<DataSource> DEFAULT_CONNECTION_CREATED_HANDLER = ds -> Flyway.configure().dataSource(ds).load().migrate();
    private final String connectionUrl;
    private final String user;
    private final String password;
    private final Consumer<DataSource> connectionCreatedHandler;
    private ProxyTestDataSource ds;

    public SingleConnectionDataSourceResource(String connectionUrl, String user, String password) {
        this(connectionUrl, user, password, DEFAULT_CONNECTION_CREATED_HANDLER);
    }

    public SingleConnectionDataSourceResource(String connectionUrl, String user, String password, Consumer<DataSource> connectionCreatedHandler) {
        this.connectionUrl = connectionUrl;
        this.user = user;
        this.password = password;
        this.connectionCreatedHandler = connectionCreatedHandler;
    }

    public ProxyTestDataSource get() {
        return ds;
    }

    @Override
    protected void before() {
        this.ds = new ProxyTestDataSource(
                new SimpleDataSource(connectionUrl, user, password, connectionCreatedHandler)
        );
    }

    @Override
    protected void after() {
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
            System.out.printf("%d. %s%n", (i + 1), sql);

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
