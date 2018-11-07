package be.crydust.spike.business;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class Repository {

    private Repository() {
        throw new UnsupportedOperationException("this class is not supposed to be instantiated");
    }

    public static DataSource lookupDataSource() throws RepositoryException {
        final String contextName = "java:comp/env";
        final String datasourceName = "jdbc/MyDataSource";
        try {
            final Context ctx = (Context) new InitialContext().lookup(contextName);
            return (DataSource) ctx.lookup(datasourceName);
        } catch (NamingException e) {
            throw new RepositoryException("Could not find DataSource '" + datasourceName + "' in context '" + contextName + "'", e);
        }
    }

    public static <T> List<T> sqlToList(DataSource ds, String sql, ParameterSetter parameterSetter, ResultSetMapper<T> resultSetMapper) throws RepositoryException {
        requireNonNull(ds, "ds");
        requireNonNull(sql, "sql");
        requireNonNull(parameterSetter, "parameterSetter");
        requireNonNull(resultSetMapper, "resultSetMapper");
        final List<T> list = new ArrayList<>();
        try (final Connection con = ds.getConnection()) {
            con.setReadOnly(true);
            try (final PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setQueryTimeout(5 /* seconds */);
                ps.setFetchSize(100 /* rows */);
                parameterSetter.accept(ps);
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(resultSetMapper.map(rs));
                    }
                }
            }
        } catch (SQLException e) {
            final String sqlOnOneLine = sql.replaceAll("[\r\n]+", " ");
            throw new RepositoryException("Could not execute query '" + sqlOnOneLine + "'", e);
        }
        return list;
    }

    public static void executeUpdate(DataSource ds, String sql, ParameterSetter parameterSetter) throws RepositoryException {
        requireNonNull(ds, "ds");
        requireNonNull(sql, "sql");
        requireNonNull(parameterSetter, "parameterSetter");
        try (final Connection con = ds.getConnection()) {
            con.setReadOnly(false);
            try (final PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setQueryTimeout(5 /* seconds */);
                parameterSetter.accept(ps);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            final String sqlOnOneLine = sql.replaceAll("[\r\n]+", " ");
            throw new RepositoryException("Could not execute update '" + sqlOnOneLine + "'", e);
        }
    }

    @FunctionalInterface
    public interface ParameterSetter {
        void accept(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    public static class RepositoryException extends RuntimeException {
        public RepositoryException() {
            super();
        }

        public RepositoryException(String message) {
            super(message);
        }

        public RepositoryException(String message, Throwable cause) {
            super(message, cause);
        }

        public RepositoryException(Throwable cause) {
            super(cause);
        }

        public RepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
