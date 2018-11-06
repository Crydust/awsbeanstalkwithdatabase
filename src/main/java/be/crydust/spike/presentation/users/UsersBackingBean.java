package be.crydust.spike.presentation.users;

import be.crydust.spike.business.users.entity.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class UsersBackingBean {

    private final Map<String, UserBackingBean> usersByName;
    private final CreateUserBackingBean createUser;

    public UsersBackingBean(Map<String, UserBackingBean> usersByName, CreateUserBackingBean createUser) {
        this.usersByName = usersByName;
        this.createUser = createUser;
    }

    public static UsersBackingBean create(List<User> users) {
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
                new CreateUserBackingBean()
        );
    }

    public Map<String, UserBackingBean> getUsersByName() {
        return usersByName;
    }

    public CreateUserBackingBean getCreateUser() {
        return createUser;
    }
}
