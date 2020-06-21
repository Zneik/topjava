package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;
    @Autowired
    private MealRepository mealRepository;

    @Test
    public void get() throws Exception {
        Meal meal = mealService.get(MEAL_1_USER.getId(), USER_ID);
        MATCHER.assertMatch(meal, MEAL_1_USER);
    }

    @Test
    public void getNotOwn() throws Exception {
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL_1_USER.getId(), ADMIN_ID));
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> mealService.get(0, USER_ID));
    }

    @Test
    public void delete() throws Exception {
        mealService.delete(MEAL_1_USER.getId(), USER_ID);
        assertNull(mealRepository.get(MEAL_1_USER.getId(), USER_ID));
    }

    @Test
    public void deleteNotOwn() throws Exception {
        assertThrows(NotFoundException.class, () -> mealService.delete(MEAL_1_USER.getId(), ADMIN_ID));
    }

    @Test
    public void deleteNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> mealService.delete(0, USER_ID));
    }

    @Test
    public void getBetweenInclusive() throws Exception {
        List<Meal> filterData = mealService.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30),
                USER_ID);
        MATCHER.assertMatch(filterData, MEAL_3_USER, MEAL_2_USER, MEAL_1_USER);
    }

    @Test
    public void getAll() throws Exception {
        List<Meal> all = mealService.getAll(USER_ID);
        MATCHER.assertMatch(all, MEAL_7_USER,
                MEAL_6_USER,
                MEAL_5_USER,
                MEAL_4_USER,
                MEAL_3_USER,
                MEAL_2_USER,
                MEAL_1_USER);
    }

    @Test
    public void update() throws Exception {
        Meal updated = getUpdated();
        mealService.update(updated, USER_ID);
        MATCHER.assertMatch(mealService.get(updated.getId(), USER_ID), updated);
    }

    @Test
    public void updateNotOwn() throws Exception {
        assertThrows(NotFoundException.class, () -> mealService.update(getUpdated(), ADMIN_ID));
    }

    @Test
    public void updateNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> {
            Meal updated = getUpdated();
            updated.setId(0);
            mealService.update(updated, USER_ID);
        });
    }

    @Test
    public void create() throws Exception {
        Meal newMeal = getNew();
        Meal created = mealService.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        MATCHER.assertMatch(created, newMeal);
        MATCHER.assertMatch(mealService.get(newId, USER_ID), newMeal);
    }
}