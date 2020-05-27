package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        try {
            filteredByCyclesOpt2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                    .forEach(System.out::println);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(filteredByStreamsOpt2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        Map<LocalDate, Integer> sumCalories = new HashMap<>();
        for (UserMeal meal : meals) {
            sumCalories.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealWithExcesses.add(
                        createUserMealWithExcess(
                                meal,
                                sumCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                        )
                );
            }
        }
        return mealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        Map<LocalDate, Integer> sumCalories = meals.stream()
                .collect(
                        Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(),
                                Collectors.summingInt(UserMeal::getCalories))
                );

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> createUserMealWithExcess(
                        meal,
                        sumCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                ))
                .collect(Collectors.toList());
    }


    public static List<UserMealWithExcess> filteredByCyclesOpt2(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) throws InterruptedException {
        Map<LocalDate, Integer> sumCaloriesByDate = new HashMap<>();
        List<Callable<Void>> tasks = new ArrayList<>();
        List<UserMealWithExcess> mealWithExcesses = Collections.synchronizedList(new ArrayList<>());
        for (UserMeal meal : meals) {
            sumCaloriesByDate.merge(meal.getDate(), meal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                tasks.add(() -> {
                    mealWithExcesses.add(createUserMealWithExcess(
                            meal,
                            sumCaloriesByDate.get(meal.getDate()) > caloriesPerDay)
                    );
                    return null;
                });
            }
        }
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.invokeAll(tasks);
        executor.shutdown();
        return mealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreamsOpt2(
            List<UserMeal> meals,
            LocalTime startTime,
            LocalTime endTime,
            int caloriesPerDay
    ) {
        return meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate))
                .values()
                .stream()
                .flatMap(mealsByDate -> {
                    boolean isExcessByDate = mealsByDate.stream()
                            .mapToInt(UserMeal::getCalories)
                            .sum() > caloriesPerDay;
                    return mealsByDate.stream()
                            .filter(meal ->
                                    TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                            .map(meal -> createUserMealWithExcess(meal, isExcessByDate));
                })
                .collect(Collectors.toList());
    }


    public static UserMealWithExcess createUserMealWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(
                userMeal.getDateTime(),
                userMeal.getDescription(),
                userMeal.getCalories(),
                excess
        );
    }

}
