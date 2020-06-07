package ru.javawebinar.topjava.util;

import java.util.List;

public interface StoreInterface<K, V> {
    List<V> getAll();

    V save(V value);

    void delete(K id);

    V get(K id);
}
