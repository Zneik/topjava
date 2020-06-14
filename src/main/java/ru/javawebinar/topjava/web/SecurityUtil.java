package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {
    public static final int USER_ONE = 1;
    public static final int USER_TWO = 2;

    private static int userId;

    static {
        userId = USER_ONE;
    }

    public static int authUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        SecurityUtil.userId = userId;
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}