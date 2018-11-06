package be.crydust.spike.presentation.users;

import java.util.Map;

public class UserBackingBean {
    private final Map<String, DeleteUserRoleBackingBean> rolesByName;
    private final AddRoleToUserBackingBean addRoleToUser;
    private final RemoveUserBackingBean removeUser;

    public UserBackingBean(Map<String, DeleteUserRoleBackingBean> rolesByName, AddRoleToUserBackingBean addRoleToUser, RemoveUserBackingBean removeUser) {
        this.rolesByName = rolesByName;
        this.addRoleToUser = addRoleToUser;
        this.removeUser = removeUser;
    }

    public Map<String, DeleteUserRoleBackingBean> getRolesByName() {
        return rolesByName;
    }

    public AddRoleToUserBackingBean getAddRoleToUser() {
        return addRoleToUser;
    }

    public RemoveUserBackingBean getRemoveUser() {
        return removeUser;
    }
}
