package be.crydust.spike.presentation;

import org.junit.Test;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
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


}