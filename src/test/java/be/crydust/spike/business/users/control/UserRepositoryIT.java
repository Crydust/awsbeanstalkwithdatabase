package be.crydust.spike.business.users.control;

import be.crydust.spike.boilerplate.SingleConnectionDataSourceResource;
import be.crydust.spike.business.users.entity.User;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UserRepositoryIT {

    @RegisterExtension
    SingleConnectionDataSourceResource dataSourceResource = new SingleConnectionDataSourceResource();

    private ProxyTestDataSource ds;
    private UserRepository repository;

    @BeforeEach
    public void setUp() {
        ds = dataSourceResource.get();
        repository = new UserRepository(ds);
    }

    @AfterEach
    public void tearDown() {
        dataSourceResource.logSqlStatements();
    }

    @Test
    public void shouldFindAllInOneExecution() {
        repository.findAll();
        assertThat(ds.getQueryExecutions(), hasSize(1));
    }

    @Test
    public void shouldFindByNameInOneExecution() {
        repository.create("a", "b", "r1");
        repository.addRoleToUser("a", "r2");
        ds.reset();

        repository.findByName("a");

        assertThat(ds.getQueryExecutions(), hasSize(1));
    }

    @Test
    public void shouldFindByNameAndLoadAllRoles() {
        repository.create("a", "b", "r1");
        repository.addRoleToUser("a", "r2");
        ds.reset();

        final User user = repository.findByName("a").get();

        assertAll(
                () -> assertThat(user.getName(), is("a")),
                () -> assertThat(user.getRoles(), containsInAnyOrder("r1", "r2"))
        );
    }

    @Test
    public void shouldCreateUserInOneExecutionWhenRoleIsEmpty() {
        repository.create("a", "b", "");
        assertThat(ds.getQueryExecutions(), hasSize(1));
    }

    @Test
    public void shouldCreateUserInTwoExecutionsWhenRoleIsNotEmpty() {
        repository.create("a", "b", "r1");
        assertThat(ds.getQueryExecutions(), hasSize(2));
    }

    @Test
    public void shouldDeleteUserRoleInOneExecution() {
        repository.deleteUserRole("a", "r1");
        assertThat(ds.getQueryExecutions(), hasSize(1));
    }

    @Test
    public void shouldAddRoleToUserInOneExecution() {
        repository.create("a", "b", "r1");
        ds.reset();

        repository.addRoleToUser("a", "r2");

        assertThat(ds.getQueryExecutions(), hasSize(1));
    }

    @Test
    public void shouldDeleteUserInTwoExecutions() {
        repository.create("a", "b", "r1");
        ds.reset();

        repository.deleteUser("a");

        assertThat(ds.getQueryExecutions(), hasSize(2));
    }

}