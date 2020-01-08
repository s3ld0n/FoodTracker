package all.that.matters.controller;

import all.that.matters.domain.Food;
import all.that.matters.domain.Role;
import all.that.matters.domain.Statistic;
import all.that.matters.domain.User;
import all.that.matters.services.FoodService;
import all.that.matters.services.StatisticService;
import all.that.matters.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private UserService userService;
    private FoodService foodService;
    private StatisticService statisticService;

    @Autowired
    public MainController(UserService userService, FoodService foodService, StatisticService statisticService) {
        this.userService = userService;
        this.foodService = foodService;
        this.statisticService = statisticService;
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @GetMapping("/main")
    public String getMain(Model model ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = (User) userDetails;

        if (user.getRoles().contains(Role.ADMIN)) {
            return "admin_main";
        }

        List<Food> usersFood = foodService.findAllByOwner(user);
        List<Statistic> todayStats = statisticService.findForToday();

        model.addAttribute("user", user);
        model.addAttribute("usersFood", usersFood);
        model.addAttribute("todayStats", todayStats);


        return "main";
    }

    @GetMapping("/login")
    public String getLoginForm() {
        return "login";
    }


    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }
}
