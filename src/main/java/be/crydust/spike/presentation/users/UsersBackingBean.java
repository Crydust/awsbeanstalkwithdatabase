package be.crydust.spike.presentation.users;

import be.crydust.spike.business.users.entity.User;
import be.crydust.spike.presentation.ErrorMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

public class UsersBackingBean {

    private final Map<String, UserBackingBean> usersByName;
    private final CreateUserBackingBean createUser;
    private final boolean error;
    private final List<ErrorMessage> errorMessages;

    private UsersBackingBean(Map<String, UserBackingBean> usersByName, CreateUserBackingBean createUser, boolean error, List<ErrorMessage> errorMessages) {
        this.usersByName = usersByName;
        this.createUser = createUser;
        this.error = error;
        this.errorMessages = errorMessages;
    }

    public static UsersBackingBean create(List<User> users) {
        return create(users, new CreateUserBackingBean(), false, emptyList());
    }

    public static UsersBackingBean create(List<User> users, CreateUserBackingBean createUser, boolean error, List<ErrorMessage> errorMessages) {
        return new UsersBackingBean(
                users.stream()
                        .collect(toMap(
                                User::getName,
                                user -> new UserBackingBean(
                                        user.getRoles().stream()
                                                .collect(toMap(
                                                        roleName -> roleName,
                                                        roleName -> new DeleteUserRoleBackingBean(user.getName(), roleName),
                                                        (a, b) -> b,
                                                        LinkedHashMap::new
                                                )),
                                        new AddRoleToUserBackingBean(user.getName(), ""),
                                        new RemoveUserBackingBean(user.getName())
                                ),
                                (a, b) -> b,
                                LinkedHashMap::new
                        )),
                createUser,
                error,
                errorMessages);
    }

    public Map<String, UserBackingBean> getUsersByName() {
        return usersByName;
    }

    public CreateUserBackingBean getCreateUser() {
        return createUser;
    }

    public boolean isError() {
        return error;
    }

    public List<ErrorMessage> getErrorMessages() {
        return errorMessages;
    }
}
