package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.base.BaseMealController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends BaseMealController {

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam(name = "id") Integer id) {
        super.delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String filter(Model model,
                         @RequestParam(name = "startDate", required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam(name = "endDate", required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         @RequestParam(name = "startTime", required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                         @RequestParam(name = "endTime", required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        model.addAttribute("meals", getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                "",
                1000);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/update")
    public String update(Model model, @RequestParam(name = "id") Integer id) {
        Meal meal = get(id);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @PostMapping
    public String save(@RequestParam(name = "id", required = false) Integer id,
                       @RequestParam(name = "dateTime")
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                       @RequestParam(name = "description") String description,
                       @RequestParam(name = "calories") Integer calories) {
        Meal meal = new Meal(dateTime, description, calories);
        if (id == null) {
            create(meal);
        } else {
            update(meal, id);
        }
        return "redirect:/meals";
    }

}
