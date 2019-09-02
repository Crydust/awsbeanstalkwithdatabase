package be.crydust.spike.business.users.boundary;

import be.crydust.spike.business.Repository;
import be.crydust.spike.business.RepositoryException;
import be.crydust.spike.business.users.WebApplicationException;
import be.crydust.spike.business.users.control.UserRepository;
import be.crydust.spike.business.users.entity.User;

import java.util.List;
import java.util.logging.Logger;

public class UserFacade {

    private static final Logger LOGGER = Logger.getLogger(UserFacade.class.getName());

    public List<User> findAll() throws WebApplicationException {
        try {
            return new UserRepository(Repository.lookupDataSource())
                    .findAll();
        } catch (RepositoryException e) {
            throw new WebApplicationException("Could not load users.", e);
        }
    }

    public User create(String name, String password, String role) {
        return new UserRepository(Repository.lookupDataSource())
                .create(name, password, role);
    }

    public boolean deleteUserRole(String name, String role) {
        return new UserRepository(Repository.lookupDataSource())
                .deleteUserRole(name, role);
    }

    public boolean addRoleToUser(String name, String role) {
        return new UserRepository(Repository.lookupDataSource())
                .addRoleToUser(name, role);
    }

    public void deleteUser(String name) {
        new UserRepository(Repository.lookupDataSource())
                .deleteUser(name);
    }
}
