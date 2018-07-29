package be.crydust.spike.users;

import java.util.Collection;
import java.util.LinkedHashSet;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

public class UsersViewModel {

    private final boolean error;
    private final String errorMessage;
    private final Collection<User> users;

    private UsersViewModel(boolean error, String errorMessage, Collection<User> users) {
        this.error = error;
        this.errorMessage = errorMessage;
        this.users = unmodifiableSet(new LinkedHashSet<>(users));
    }

    public static UsersViewModel createSuccess(Collection<User> users) {
        return new UsersViewModel(false, "", users);
    }

    public static UsersViewModel createError(String errorMessage) {
        return new UsersViewModel(true, errorMessage, emptySet());
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Collection<User> getUsers() {
        return users;
    }

}
