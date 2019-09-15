package be.crydust.spike.business.users.boundary;

import be.crydust.spike.business.RepositoryException;
import be.crydust.spike.business.users.WebApplicationException;
import be.crydust.spike.business.users.control.UserRepository;
import be.crydust.spike.business.users.entity.User;

import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Logger;

public class UserFacade {

    private static final Logger LOGGER = Logger.getLogger(UserFacade.class.getName());

    private final DataSource ds;

    public UserFacade(DataSource ds) {
        this.ds = ds;
    }

    public List<User> findAll() throws WebApplicationException {
        try {
            return new UserRepository(this.ds).findAll();
        } catch (RepositoryException e) {
            throw new WebApplicationException("Could not load users.", e);
        }
    }

    public User create(String name, String password, String role) {
        return new UserRepository(this.ds).create(name, password, role);
    }

    public boolean deleteUserRole(String name, String role) {
        return new UserRepository(this.ds).deleteUserRole(name, role);
    }

    public boolean addRoleToUser(String name, String role) {
        return new UserRepository(this.ds).addRoleToUser(name, role);
    }

    public boolean deleteUser(String name) {
        return new UserRepository(this.ds).deleteUser(name);
    }
}
