package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MealsStoreMemory implements StoreInterface<Integer, Meal> {

    private final AtomicInteger counter = new AtomicInteger(1);
    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();

    public MealsStoreMemory() {
        Integer id = counter.getAndIncrement();
        meals.put(id, new Meal(id,
                LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0),
                "Завтрак",
                500));
        id = counter.getAndIncrement();
        meals.put(id, new Meal(id,
                LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0),
                "Обед",
                1000));
        id = counter.getAndIncrement();
        meals.put(id, new Meal(id,
                LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0),
                "Ужин",
                500));
        id = counter.getAndIncrement();
        meals.put(id, new Meal(id,
                LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0),
                "Еда на граничное значение",
                100));
        id = counter.getAndIncrement();
        meals.put(id, new Meal(id,
                LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0),
                "Завтрак",
                1000));
        id = counter.getAndIncrement();
        meals.put(id, new Meal(id,
                LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0),
                "Обед",
                500));
        id = counter.getAndIncrement();
        meals.put(id, new Meal(id,
                LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0),
                "Ужин",
                410));
    }

    @Override
    public List<Meal> getAll() {
        return meals.values()
                .stream()
                .sorted(Comparator.comparing(Meal::getDateTime))
                .collect(Collectors.toList());
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
