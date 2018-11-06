package be.crydust.spike.presentation.users;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RemoveUserBackingBean {
    @NotBlank
    @Size(min = 1, max = 64)
    private String name;

    public RemoveUserBackingBean(@NotBlank @Size(min = 1, max = 64) String name) {
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

}
