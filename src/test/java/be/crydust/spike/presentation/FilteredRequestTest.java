package be.crydust.spike.presentation;

import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class FilteredRequestTest {

    @Test
    public void readFromEmptyMap() {
        final String button = "";
        final Map<String, String[]> parameters = emptyMap();
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndErrorMessages<DummyBean> inputAndErrorMessages = filteredRequest.read(new DummyBean());

        final List<ErrorMessage> errorMessages = inputAndErrorMessages.getErrorMessages();
        assertThat(errorMessages, is(not(empty())));
        assertThat(errorMessages.stream().map(ErrorMessage::getFieldId).collect(toList()), contains("name"));
    }

    @Test
    public void readFromFilledMap() {
        final String button = "";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("name", new String[]{"example"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndErrorMessages<DummyBean> inputAndErrorMessages = filteredRequest.read(new DummyBean());

        assertThat(inputAndErrorMessages.getErrorMessages(), is(empty()));
        assertThat(inputAndErrorMessages.getInput().getName(), is("example"));
    }

    @Test
    public void readFromFilledMapWithPrefix() {
        final String button = "prefix:";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("prefix:name", new String[]{"example"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndErrorMessages<DummyBean> inputAndErrorMessages = filteredRequest.read(new DummyBean());

        assertThat(inputAndErrorMessages.getErrorMessages(), is(empty()));
        assertThat(inputAndErrorMessages.getInput().getName(), is("example"));
    }

    @Test
    public void readFromFilledMapWithPrefixAndSuffix() {
        final String button = "prefix:a=b";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("prefix:name", new String[]{"example"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndErrorMessages<DummyBean> inputAndErrorMessages = filteredRequest.read(new DummyBean());

        assertThat(inputAndErrorMessages.getErrorMessages(), is(empty()));
        assertThat(inputAndErrorMessages.getInput().getName(), is("example"));
    }

    @Test
    public void readFromButton() {
        final String button = "button:name=example";
        final Map<String, String[]> parameters = emptyMap();
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndErrorMessages<DummyBean> inputAndErrorMessages = filteredRequest.read(new DummyBean());

        assertThat(inputAndErrorMessages.getErrorMessages(), is(empty()));
        assertThat(inputAndErrorMessages.getInput().getName(), is("example"));
    }

    @Test
    public void readFromButtonWithCommaInValue() {
        final String button = "button:name=" + FilteredRequest.escape("te,s\\t");
        final Map<String, String[]> parameters = emptyMap();
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndErrorMessages<DummyBean> inputAndErrorMessages = filteredRequest.read(new DummyBean());

        assertThat(inputAndErrorMessages.getErrorMessages(), is(empty()));
        assertThat(inputAndErrorMessages.getInput().getName(), is("te,s\\t"));
    }

    @Test
    public void readFromButtonAndFromParameters() {
        final String button = "prefix:name=example,roles=a";
        final Map<String, String[]> parameters = new LinkedHashMap<>();
        parameters.put("prefix:roles", new String[]{"b", "c"});
        final FilteredRequest filteredRequest = new FilteredRequest(button, parameters);

        final InputAndErrorMessages<DummyBean> inputAndErrorMessages = filteredRequest.read(new DummyBean());

        assertThat(inputAndErrorMessages.getErrorMessages(), is(empty()));
        assertThat(inputAndErrorMessages.getInput().getName(), is("example"));
        assertThat(inputAndErrorMessages.getInput().getRoles(), contains("a", "b", "c"));
    }

    public static class DummyBean implements Validateable {
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

        @Override
        public List<ErrorMessage> validate() {
            final List<ErrorMessage> errorMessages = new ArrayList<>();
            if (name == null || name.isEmpty()) {
                errorMessages.add(new ErrorMessage("name", "Cannot be blank"));
            } else if (name.length() < 1 || name.length() > 8) {
                errorMessages.add(new ErrorMessage("name", String.format("Length must be between %d and %d", 1, 8)));
            }
            return unmodifiableList(errorMessages);
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


}