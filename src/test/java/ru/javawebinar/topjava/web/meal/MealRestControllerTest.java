package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.TestUtil.readListFromJsonMvcResult;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.util.MealsUtil.createTo;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;

class MealRestControllerTest extends AbstractControllerTest {
    private final static String REST_URL = MealRestController.REST_URL + "/";

    @Autowired
    private MealService mealService;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> assertThat(readListFromJsonMvcResult(mvcResult, MealTo.class))
                        .isEqualTo(getTos(MEALS, USER.getCaloriesPerDay())));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(MEAL1));
    }

    @Test
    void createWithLocation() throws Exception {
        Meal newMeal = getNew();
        ResultActions resultActions = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        Meal created = readFromJson(resultActions, Meal.class);
        newMeal.setId(created.getId());
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(created.getId(), USER_ID), newMeal);
    }

    @Test
    void update() throws Exception {
        Meal updatedMeal = getUpdated();
        perform(put(REST_URL + updatedMeal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedMeal)))
                .andDo(print())
                .andExpect(status().isNoContent());
        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, USER_ID), getUpdated());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void getBetweenWithoutParams() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> assertThat(readListFromJsonMvcResult(mvcResult, MealTo.class))
                        .isEqualTo(getTos(MEALS, USER.getCaloriesPerDay())));
    }

    @Test
    void getBetweenWithParams() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL
                + "filter?startDate=2020-01-30&startTime=10:00&endDate=2020-01-31&endTime=14:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> assertThat(readListFromJsonMvcResult(mvcResult, MealTo.class))
                        .isEqualTo(List.of(MealsUtil.createTo(MEAL6, true),
                                MealsUtil.createTo(MEAL5, true),
                                MealsUtil.createTo(MEAL2, false),
                                MealsUtil.createTo(MEAL1, false))));
    }

    @Test
    void getBetweenWithEmptyParams() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL
                + "filter?startDate=&startTime=&endDate=&endTime="))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> assertThat(readListFromJsonMvcResult(mvcResult, MealTo.class))
                        .isEqualTo(getTos(MEALS, USER.getCaloriesPerDay())));
    }

    @Test
    void getBetweenWithStartDateEmpty() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL
                + "filter?startDate="))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> assertThat(readListFromJsonMvcResult(mvcResult, MealTo.class))
                        .isEqualTo(getTos(MEALS, USER.getCaloriesPerDay())));
    }

    @Test
    void getBetweenWithWithoutStartTime() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/filter?startDate=2020-01-30&endDate=2020-01-31&endTime=11:00"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> assertThat(readListFromJsonMvcResult(mvcResult, MealTo.class))
                        .isEqualTo(List.of(createTo(MEAL5, true),
                                createTo(MEAL4, true),
                                createTo(MEAL1, false))));
    }

    @Test
    void getBetweenEmptyList() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/filter?startDate=2020-07-30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> assertThat(readListFromJsonMvcResult(mvcResult, MealTo.class))
                        .isEqualTo(Collections.emptyList()));
    }
}