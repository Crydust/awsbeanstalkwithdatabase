package be.crydust.spike.business.users.entity;

import java.util.Objects;
import java.util.Set;

public class User {
    private final String name;
    private final Set<String> roles;

    public User(String name, Set<String> roles) {
        this.name = name;
        this.roles = Set.copyOf(roles);
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
