package be.crydust.spike.presentation.users;

import be.crydust.spike.presentation.ErrorMessage;
import be.crydust.spike.presentation.Validateable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class AddRoleToUserBackingBean implements Validateable {
    private String name;
    private String role;

    public AddRoleToUserBackingBean(String name, String role) {
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

    @Override
    public List<ErrorMessage> validate() {
        final List<ErrorMessage> errorMessages = new ArrayList<>();
        if (name == null || name.isEmpty()) {
            errorMessages.add(new ErrorMessage("name", "Cannot be blank"));
        } else if (name.length() < 1 || name.length() > 64) {
            errorMessages.add(new ErrorMessage("name", String.format("Length must be between %d and %d", 1, 64)));
        }
        if (role == null || role.isEmpty()) {
            errorMessages.add(new ErrorMessage("role", "Cannot be blank"));
        } else if (role.length() < 1 || role.length() > 64) {
            errorMessages.add(new ErrorMessage("role", String.format("Length must be between %d and %d", 1, 64)));
        }
        return unmodifiableList(errorMessages);
    }

    @Override
    public String toString() {
        return "AddRoleToUserBackingBean{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
