package com.example.todoapp;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final TodoMapper todoMapper;

    public HomeController(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "やること管理");
        return "index";
    }
    @GetMapping("/todos")
    public String todos(Model model) {
        List<Todo> todos = todoMapper.findAll();
        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/todos/new")
    public String createForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "create";
    }

    @PostMapping("/todos/confirm")
    public String createConfirm(@ModelAttribute Todo todo, Model model) {
        model.addAttribute("todo", todo);
        return "create-confirm";
    }

    @PostMapping("/todos/new")
    public String createRewrite(@ModelAttribute Todo todo, Model model) {
        model.addAttribute("todo", todo);
        return "create";
    }
}
