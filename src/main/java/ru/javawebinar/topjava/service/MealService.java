package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {
    private final MealRepository repository;

    @Autowired
    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal) {
        return repository.save(meal);
    }

    public void delete(int id, int userId) {
        checkNotFoundWithId(repository.delete(id, userId), id);
    }

    public Meal get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public List<MealTo> getAll(int userId, int userCaloriesPerDay) {
        return new ArrayList<>(MealsUtil.getTos(repository.getAll(userId), userCaloriesPerDay));
    }

    public List<MealTo> getAllByDateTime(int userId,
                                         int userCaloriesPerDay,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         LocalTime startTime,
                                         LocalTime endTime) {
        final Collection<Meal> userMealsByDate = repository.getAllByDate(userId, startDate, endDate);
        return MealsUtil.getFilteredTos(userMealsByDate, userCaloriesPerDay, startTime, endTime);
    }

    public void update(Meal meal) {
        checkNotFound(repository.save(meal), meal.getId().toString());
    }

}