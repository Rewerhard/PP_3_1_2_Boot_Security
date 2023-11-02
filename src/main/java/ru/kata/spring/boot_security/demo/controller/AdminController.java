package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;



@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;


    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public String showUserList(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "list-users";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "user-form";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult result,
                           Model model) {
        if (result.hasErrors()) { //valid
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.findAll());
            return "user-form";
        }
        String encode = user.getPassword();        //new user
        userService.passwordChanged(user, encode);
        userService.add(user);
        return "redirect:/admin/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("userId") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "user-form-for-update";
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.findAll());
            return "user-form-for-update";
        }
        String encode = user.getPassword();
        if (encode.isEmpty()) {
            user.setPassword(userService.getUserById(user.getId()).getPassword());
        } else {
            userService.passwordChanged(user, encode);
        }
        userService.update(user);
        return "redirect:/admin/list";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/list";
    }
}
