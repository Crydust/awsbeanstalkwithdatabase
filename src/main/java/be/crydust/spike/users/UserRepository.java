package be.crydust.spike.users;

import javax.sql.DataSource;
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

    public List<User> findAll(){
        //language=PostgreSQL
        String sql = "select users.user_name, user_roles.role_name\n" +
                "from users\n" +
                "left join user_roles\n" +
                "on users.user_name = user_roles.user_name\n";

        final List<User> usersWithOneRole = RepositoryCommons.sqlToList(
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

}
