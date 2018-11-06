package be.crydust.spike.presentation.users;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AddRoleToUserBackingBean {
    @NotBlank
    @Size(min = 1, max = 64)
    private String name;
    @NotBlank
    @Size(min = 1, max = 64)
    private String role;

    public AddRoleToUserBackingBean(@NotBlank @Size(min = 1, max = 64) String name, @NotBlank @Size(min = 1, max = 64) String role) {
        this.name = name;
        this.role = role;
    }

    public AddRoleToUserBackingBean() {
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
}
