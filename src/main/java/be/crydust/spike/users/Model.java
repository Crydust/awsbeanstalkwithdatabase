package be.crydust.spike.users;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public class Model {

    private final Set<User> users;

    public Model(Set<User> users) {
        this.users = unmodifiableSet(new HashSet<>(users));
    }

    public Set<User> getUsers() {
        return users;
    }

}
