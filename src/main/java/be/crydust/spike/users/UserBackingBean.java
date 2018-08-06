package be.crydust.spike.users;

import java.util.Map;

public class UserBackingBean {
    private final Map<String, DeleteUserRoleBackingBean> rolesByName;
    private final AddRoleToUserBackingBean addRoleToUse;
    private final RemoveUserBackingBean removeUser;

    public UserBackingBean(Map<String, DeleteUserRoleBackingBean> rolesByName, AddRoleToUserBackingBean addRoleToUse, RemoveUserBackingBean removeUser) {
        this.rolesByName = rolesByName;
        this.addRoleToUse = addRoleToUse;
        this.removeUser = removeUser;
    }

    public Map<String, DeleteUserRoleBackingBean> getRolesByName() {
        return rolesByName;
    }

    public AddRoleToUserBackingBean getAddRoleToUse() {
        return addRoleToUse;
    }

    public RemoveUserBackingBean getRemoveUser() {
        return removeUser;
    }
}
