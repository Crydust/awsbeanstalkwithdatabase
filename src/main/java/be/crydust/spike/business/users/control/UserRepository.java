package be.crydust.spike.business.users.control;

import be.crydust.spike.business.Repository;
import be.crydust.spike.business.users.WebApplicationException;
import be.crydust.spike.business.users.entity.User;
import org.apache.catalina.realm.SecretKeyCredentialHandler;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;

public class UserRepository {

    private final DataSource ds;

    public UserRepository(DataSource ds) {
        this.ds = ds;
    }

    private static String encodeWithTomcat(String credentials) {
        try {
            SecretKeyCredentialHandler h = new SecretKeyCredentialHandler();
            h.setAlgorithm("PBKDF2WithHmacSHA256");
            h.setIterations(185000);
            h.setSaltLength(8);
            h.setKeyLength(256);
            return h.mutate(credentials);
        } catch (NoSuchAlgorithmException e) {
            throw new WebApplicationException("failed to encrypt password", e);
        }
    }

    public List<User> findAll() {
        //language=PostgreSQL
        String sql = "select users.user_name, user_roles.role_name\n" +
                "from users\n" +
                "left join user_roles\n" +
                "on users.user_name = user_roles.user_name\n";

        final List<User> usersWithOneRole = Repository.sqlToList(
                ds,
                sql,
                ps -> {
                },
                rs -> {
                    String user_name = rs.getString("user_name");
                    if (rs.wasNull()) user_name = null;
                    String role_name = rs.getString("role_name");
                    if (rs.wasNull()) role_name = null;
                    final Set<String> roles = role_name == null ? emptySet() : singleton(role_name);
                    return new User(user_name, roles);
                });

        final Map<String, Set<String>> userNameToRolesMap = usersWithOneRole
                .stream()
                .collect(Collectors.toMap(
                        User::getName,
                        User::getRoles,
                        (a, b) -> {
                            final Set<String> roles = new HashSet<>(a);
                            roles.addAll(b);
                            return unmodifiableSet(roles);
                        }));

        final List<User> users = userNameToRolesMap
                .entrySet()
                .stream()
                .map(it -> new User(it.getKey(), it.getValue()))
                .collect(toList());

        return users;
    }

    public User create(String name, String password, String role) {
        //language=PostgreSQL
        String sql = "insert into users (user_name, user_pass)\n" +
                "values (?, ?)";
        Repository.executeUpdate(ds, sql, (ps) -> {
            ps.setString(1, name);
            ps.setString(2, encodeWithTomcat(password));
        });
        if (role == null || role.isEmpty()) {
            return new User(name, emptySet());
        }
        //language=PostgreSQL
        String sql2 = "insert into user_roles (user_name, role_name)\n" +
                "values (?, ?)";
        Repository.executeUpdate(ds, sql2, (ps) -> {
            ps.setString(1, name);
            ps.setString(2, role);
        });
        return new User(name, singleton(role));
    }

    public boolean deleteUserRole(String name, String role) {
        //language=PostgreSQL
        String sql = "delete from user_roles\n" +
                "where user_name = ? and role_name = ?";
        return Repository.executeUpdate(ds, sql, (ps) -> {
            ps.setString(1, name);
            ps.setString(2, role);
        }) == 1;
    }

    public boolean addRoleToUser(String name, String role) {
        //language=PostgreSQL
        String sql = "insert into user_roles (user_name, role_name)\n" +
                "values (?, ?)";
        return Repository.executeUpdate(ds, sql, (ps) -> {
            ps.setString(1, name);
            ps.setString(2, role);
        }) == 1;
    }

    public boolean deleteUser(String name) {
        //language=PostgreSQL
        String sql1 = "delete from user_roles\n" +
                "where user_name = ?";
        Repository.executeUpdate(ds, sql1, (ps) -> {
            ps.setString(1, name);
        });
        //language=PostgreSQL
        String sql2 = "delete from users\n" +
                "where user_name = ?";
        return Repository.executeUpdate(ds, sql2, (ps) -> {
            ps.setString(1, name);
        }) == 1;
    }

}
