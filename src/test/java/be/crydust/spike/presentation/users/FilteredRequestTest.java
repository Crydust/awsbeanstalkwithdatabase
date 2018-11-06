package be.crydust.spike.presentation.users;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class FilteredRequestTest {

    public static class DummyBean {
        @NotBlank
        @Size(min = 1, max = 8)
        private String name;
        private List<String> roles = new AutoGrowingList<>(new ArrayList<>());

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }

    public static class AutoGrowingList<E> extends AbstractList<E> implements List<E> {

        private final List<E> innerList;

        public AutoGrowingList(List<E> innerList) {
            this.innerList = innerList;
        }

        @Override
        public E get(int index) {
            return this.innerList.get(index);
        }

        @Override
        public int size() {
            return this.innerList.size();
        }

        @Override
        public E set(int index, E element) {
            while (index >= size()) {
                innerList.add(null);
            }
            return this.innerList.set(index, element);
        }
    }

    @Test
    public void readFromEmptyMap() {
        final String button = "";
        final Map<String, String[]> parameters = emptyMap();
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndViolations<DummyBean> inputAndViolations = filteredRequest.read(new DummyBean());
        final Set<ConstraintViolation<DummyBean>> violations = inputAndViolations.getViolations();

        assertThat(violations, is(not(empty())));
        assertThat(violationsToPathStrings(violations), contains("/name"));
    }

    @Test
    public void readFromFilledMap() {
        final String button = "";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("name", new String[]{"example"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndViolations<DummyBean> inputAndViolations = filteredRequest.read(new DummyBean());

        assertThat(inputAndViolations.getViolations(), is(empty()));
        assertThat(inputAndViolations.getInput().getName(), is("example"));
    }

    @Test
    public void readFromFilledMapWithPrefix() {
        final String button = "prefix:";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("prefix:name", new String[]{"example"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndViolations<DummyBean> inputAndViolations = filteredRequest.read(new DummyBean());

        assertThat(inputAndViolations.getViolations(), is(empty()));
        assertThat(inputAndViolations.getInput().getName(), is("example"));
    }

    @Test
    public void readFromFilledMapWithPrefixAndSuffix() {
        final String button = "prefix:a=b";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("prefix:name", new String[]{"example"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndViolations<DummyBean> inputAndViolations = filteredRequest.read(new DummyBean());

        assertThat(inputAndViolations.getViolations(), is(empty()));
        assertThat(inputAndViolations.getInput().getName(), is("example"));
    }

    @Test
    public void readFromButton() {
        final String button = "button:name=example";
        final Map<String, String[]> parameters = emptyMap();
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndViolations<DummyBean> inputAndViolations = filteredRequest.read(new DummyBean());

        assertThat(inputAndViolations.getViolations(), is(empty()));
        assertThat(inputAndViolations.getInput().getName(), is("example"));
    }

    @Test
    public void readFromButtonAndFromParameters() {
        final String button = "prefix:name=example,roles=a";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("prefix:roles", new String[]{"b", "c"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndViolations<DummyBean> inputAndViolations = filteredRequest.read(new DummyBean());

        assertThat(inputAndViolations.getViolations(), is(empty()));
        assertThat(inputAndViolations.getInput().getName(), is("example"));
        assertThat(inputAndViolations.getInput().getRoles(), contains("a", "b", "c"));
    }

    private static List<String> violationsToPathStrings(Set<ConstraintViolation<DummyBean>> violations) {
        final List<String> violationPaths = new ArrayList<>(violations.size());
        for (ConstraintViolation<DummyBean> violation : violations) {
            final Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();
            final StringBuilder sb = new StringBuilder("/");
            while (iterator.hasNext()) {
                sb.append(iterator.next().getName());
                if (iterator.hasNext()) {
                    sb.append("/");
                }
            }
            violationPaths.add(sb.toString());
        }
        return violationPaths;
    }

}