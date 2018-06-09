package be.crydust.spike;

import org.flywaydb.core.Flyway;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener()
public class FlywayListener implements ServletContextListener {

    @Resource(name = "jdbc/exampleDB")
    private DataSource ds;

//    private static DataSource lookupDataSource() throws NamingException {
//        final Context ctx = (Context) new InitialContext().lookup("java:comp/env");
//        return (DataSource) ctx.lookup("jdbc/exampleDB");
//    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.migrate();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // NOOP
    }
}
