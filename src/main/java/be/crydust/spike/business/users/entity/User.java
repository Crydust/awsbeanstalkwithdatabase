package be.crydust.spike.business.users.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public class User {
    private final String name;
    private final Set<String> roles;

    public User(String name, Set<String> roles) {
        this.name = name;
        this.roles = unmodifiableSet(new HashSet<>(roles));
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
