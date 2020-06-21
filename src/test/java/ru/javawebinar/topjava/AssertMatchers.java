package ru.javawebinar.topjava;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertMatchers<T> {
    private final String[] ignoringFields;

    public AssertMatchers(String... ignoringFields) {
        this.ignoringFields = ignoringFields;
    }

    public void assertMatch(T actual, T expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, this.ignoringFields);
    }

    public void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields(this.ignoringFields).isEqualTo(expected);
    }
}
