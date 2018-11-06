package be.crydust.spike.presentation.sample;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener()
public class FlywayListener implements ServletContextListener {

    @Resource(name = "jdbc/MyDataSource")
    private DataSource ds;

//    private static DataSource lookupDataSource() throws NamingException {
//        final Context ctx = (Context) new InitialContext().lookup("java:comp/env");
//        return (DataSource) ctx.lookup("jdbc/MyDataSource");
//    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        new Flyway(new FluentConfiguration().dataSource(ds))
                .migrate();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // NOOP
    }
}
