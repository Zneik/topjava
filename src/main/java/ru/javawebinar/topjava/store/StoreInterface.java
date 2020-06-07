package ru.javawebinar.topjava.store;

import java.util.List;

public interface StoreInterface<K, V> {
    List<V> getAll();

    V save(V value);

    void delete(K id);

    V get(K id);
}
