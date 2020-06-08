package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.store.MealsStoreMemory;
import ru.javawebinar.topjava.store.StoreInterface;
import ru.javawebinar.topjava.util.MealsUtil;

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
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("meals", MealsUtil.filteredByStreams(mealsStoreMemory.getAll(),
                    LocalTime.MIN,
                    LocalTime.MAX,
                    MealsUtil.CALORIES_PER_DAY));
            log.debug(Actions.VIEW);
            request.getRequestDispatcher("meals.jsp").forward(request, response);
        } else {
            String id = request.getParameter("id");
            switch (action) {
                case Actions.EDIT_VIEW:
                    if (id != null) {
                        request.setAttribute("meals", MealsUtil.filteredByStreams(mealsStoreMemory.getAll(),
                                LocalTime.MIN,
                                LocalTime.MAX,
                                MealsUtil.CALORIES_PER_DAY));
                        log.debug(Actions.EDIT_VIEW);
                        request.setAttribute("editMeal", mealsStoreMemory.get(Integer.valueOf(id)));
                        request.getRequestDispatcher("meals.jsp").forward(request, response);
                    } else {
                        response.sendRedirect("meals");
                    }
                    break;
                case Actions.DELETE:
                    if (id != null) {
                        log.debug(Actions.DELETE);
                        mealsStoreMemory.delete(Integer.valueOf(id));
                    }
                    response.sendRedirect("meals");
                    break;
                default:
                    response.sendRedirect("meals");
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        log.debug(Actions.SAVE);
        Meal meal = parseRequestToMeal(request);
        mealsStoreMemory.save(meal);
        response.sendRedirect("meals");
    }

    private Meal parseRequestToMeal(HttpServletRequest request) {
        Integer id = null;
        if (request.getParameter("meal_id") != null &&
                !request.getParameter("meal_id").isEmpty()) {
            id = Integer.valueOf(request.getParameter("meal_id"));
        }
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"), dateTimeFormatter);
        String description = request.getParameter("description");
        Integer calories = Integer.valueOf(request.getParameter("calories"));
        return new Meal(id, dateTime, description, calories);
    }

    public static class Actions {
        public static final String SAVE = "save";
        public static final String EDIT_VIEW = "edit_view";
        public static final String DELETE = "delete";
        public static final String VIEW = "view";
    }
}
