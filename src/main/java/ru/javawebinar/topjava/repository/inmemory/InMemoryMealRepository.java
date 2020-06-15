package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    //[1 => [2 => 3]]   1 - id user, 2 - id meal, 3 - user meal
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, SecurityUtil.USER_ONE));
        save(new Meal(LocalDateTime.of(2020, Month.JUNE, 15, 10, 0),
                "Завтрак",
                500), SecurityUtil.USER_TWO);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> userMeals = repository.computeIfAbsent(userId, ConcurrentHashMap::new);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            userMeals.put(meal.getId(), meal);
            return meal;
        }
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> {
            meal.setUserId(userId);
            return meal;
        });
    }

    @Override
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null && userMeals.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ? userMeals.get(id) : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getWithFilter(userId, meal -> true);
    }

    @Override
    public List<Meal> getAllByDate(int userId, LocalDate start, LocalDate end) {
        return getWithFilter(userId, meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDate(), start, end));
    }

    private List<Meal> getWithFilter(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ? userMeals.values()
                .stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

}

