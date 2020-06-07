package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsStoreMemory;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.StoreInterface;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealsServlet extends HttpServlet {
    private static final Logger log = getLogger(MealsServlet.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final StoreInterface<Integer, Meal> mealsStoreMemory = new MealsStoreMemory();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("meals",
                MealsUtil.filteredByStreams(mealsStoreMemory.getAll(),
                        LocalTime.MIN,
                        LocalTime.MAX,
                        MealsUtil.CALORIES_PER_DAY));

        String actionType = request.getParameter("action");
        if (actionType != null && !actionType.isEmpty()) {
            Integer id = Integer.valueOf(request.getParameter("id"));
            if (actionType.equals(Actions.DELETE)) {
                log.debug(Actions.DELETE);
                mealsStoreMemory.delete(id);
                response.sendRedirect("meals");
                return;
            } else {
                log.debug(Actions.EDIT);
                request.setAttribute("editMeal", mealsStoreMemory.get(id));
            }
        }

        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        Meal meal = parseRequestToMeal(request);
        mealsStoreMemory.save(meal);

        response.sendRedirect("meals");
    }

    private Meal parseRequestToMeal(HttpServletRequest request) {
        Integer id = null;
        if (request.getParameter("meal_id") != null && !request.getParameter("meal_id").isEmpty())
            id = Integer.valueOf(request.getParameter("meal_id"));

        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"), dateTimeFormatter);

        String description = request.getParameter("description");

        Integer calories = Integer.valueOf(request.getParameter("calories"));

        return new Meal(id, dateTime, description, calories);
    }

    public static class Actions {
        public static final String EDIT = "edit";
        public static final String DELETE = "delete";
    }
}
