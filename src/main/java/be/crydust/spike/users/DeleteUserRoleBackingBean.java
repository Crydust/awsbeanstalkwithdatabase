package be.crydust.spike.users;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class DeleteUserRoleBackingBean {
    @NotBlank
    @Size(min = 1, max = 64)
    private String name;
    @NotBlank
    @Size(min = 1, max = 64)
    private String role;

    public DeleteUserRoleBackingBean(@NotBlank @Size(min = 1, max = 64) String name, @NotBlank @Size(min = 1, max = 64) String role) {
        this.name = name;
        this.role = role;
    }

    public DeleteUserRoleBackingBean() {
        this(null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "DeleteUserRoleBackingBean{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
