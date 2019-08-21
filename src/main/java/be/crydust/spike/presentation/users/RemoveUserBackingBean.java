package be.crydust.spike.presentation.users;

import be.crydust.spike.presentation.ErrorMessage;
import be.crydust.spike.presentation.Validateable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RemoveUserBackingBean implements Validateable {
    private String name;

    public RemoveUserBackingBean(String name) {
        this.name = name;
    }

    public RemoveUserBackingBean() {
        this(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<ErrorMessage> validate() {
        final List<ErrorMessage> errorMessages = new ArrayList<>();
        if (name == null || name.isEmpty()) {
            errorMessages.add(new ErrorMessage("name", "Cannot be blank"));
        } else if (name.length() < 1 || name.length() > 64) {
            errorMessages.add(new ErrorMessage("name", String.format("Length must be between %d and %d", 1, 64)));
        }
        return unmodifiableList(errorMessages);
    }
}
