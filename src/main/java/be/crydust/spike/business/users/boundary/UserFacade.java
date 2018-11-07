package be.crydust.spike.business.users.boundary;

import be.crydust.spike.business.Repository;
import be.crydust.spike.business.users.control.UserRepository;
import be.crydust.spike.business.users.entity.User;

import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.logging.Logger;

public class UserFacade {

    private static final Logger LOGGER = Logger.getLogger(UserFacade.class.getName());

    public List<User> findAll() throws WebApplicationException {
        try {
            return new UserRepository(Repository.lookupDataSource())
                    .findAll();
        } catch (Repository.RepositoryException e) {
            throw new WebApplicationException("Could not load users.", e);
        }
    }

    public User create(String name, String password, String role) {
        return new UserRepository(Repository.lookupDataSource())
                .create(name, password, role);
    }
}
