package ru.javawebinar.topjava.store;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsStoreMemory implements StoreInterface<Integer, Meal> {

    private final AtomicInteger counter = new AtomicInteger(1);
    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();

    public MealsStoreMemory() {
        save(new Meal(null,
                LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0),
                "Завтрак",
                500));
        save(new Meal(null,
                LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0),
                "Обед",
                1000));
        save(new Meal(null,
                LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0),
                "Ужин",
                500));
        save(new Meal(null,
                LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0),
                "Еда на граничное значение",
                100));
        save(new Meal(null,
                LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0),
                "Завтрак",
                1000));
        save(new Meal(null,
                LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0),
                "Обед",
                500));
        save(new Meal(null,
                LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0),
                "Ужин",
                410));
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal save(Meal value) {
        if (value.getId() == null) {
            Integer id = counter.getAndIncrement();
            value.setId(id);
            meals.put(id, value);
        } else {
            if (meals.containsKey(value.getId())) {
                meals.put(value.getId(), value);
            } else {
                return null;
            }
        }
        return value;
    }

    @Override
    public void delete(Integer id) {
        meals.remove(id);
    }

    @Override
    public Meal get(Integer id) {
        return meals.get(id);
    }
}
