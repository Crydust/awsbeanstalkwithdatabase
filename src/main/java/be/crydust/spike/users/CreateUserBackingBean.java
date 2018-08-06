package be.crydust.spike.users;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateUserBackingBean {
    @NotBlank
    @Size(min = 1, max = 64)
    private String name;
    @NotBlank
    private String pasword;
    @NotBlank
    @Size(min = 1, max = 64)
    private String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasword() {
        return pasword;
    }

    public void setPasword(String pasword) {
        this.pasword = pasword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
