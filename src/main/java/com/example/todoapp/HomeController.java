package com.example.todoapp;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;

@Controller
public class HomeController {

    private static final int[] RANK_THRESHOLDS = {0, 100, 300, 500, 800};
    private static final String[] RANK_NAMES = {"Bronze", "Silver", "Gold", "Platinum", "Diamond"};

    private final TodoService todoService;

    public HomeController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "やること管理");
        return "index";
    }
    @GetMapping("/todos")
    public String todos(@RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(name = "showCompleted", defaultValue = "false") boolean showCompleted,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "0") int trash, Model model) {
        if (!"desc".equals(order)) {
            order = "asc";
        }
        page = Math.max(page, 1);
        int pageSize = 10;
        boolean showTrash = trash == 1;
        int totalCount = todoService.count(keyword, category, showCompleted, showTrash);
        int totalPages = Math.max((totalCount + pageSize - 1) / pageSize, 1);
        page = Math.min(page, totalPages);
        List<Todo> todos = todoService.searchPage(keyword, category, order, showCompleted, showTrash, page, pageSize);
        model.addAttribute("todos", todos);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("order", order);
        model.addAttribute("showCompleted", showCompleted);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("trash", trash);
        int experiencePoints = todoService.getExperiencePoints();
        int rankIndex = 0;
        for (int i = 0; i < RANK_THRESHOLDS.length; i++) {
            if (experiencePoints >= RANK_THRESHOLDS[i]) {
                rankIndex = i;
            }
        }
        int nextRankIndex = Math.min(rankIndex + 1, RANK_NAMES.length - 1);
        int nextExperience = RANK_THRESHOLDS[nextRankIndex];
        int progress = nextRankIndex == rankIndex
                ? 100
                : Math.min(100, experiencePoints * 100 / nextExperience);

        model.addAttribute("experiencePoints", experiencePoints);
        model.addAttribute("experienceRank", RANK_NAMES[rankIndex]);
        model.addAttribute("nextExperienceRank", RANK_NAMES[nextRankIndex]);
        model.addAttribute("nextExperience", nextExperience);
        model.addAttribute("experienceProgress", progress);
        model.addAttribute("experienceRemaining", Math.max(0, nextExperience - experiencePoints));
        return "todos";
    }

    @GetMapping("/todos/new")
    public String createForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "create";
    }

    @PostMapping("/todos/confirm")
    public String createConfirm(@Valid @ModelAttribute Todo todo, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        model.addAttribute("todo", todo);
        return "create-confirm";
    }

    @PostMapping("/todos/new")
    public String createRewrite(@ModelAttribute Todo todo, Model model) {
        model.addAttribute("todo", todo);
        return "create";
    }

    @PostMapping("/todos")
    public String create(@ModelAttribute Todo todo, RedirectAttributes redirectAttributes) {
        todoService.create(todo);
        redirectAttributes.addFlashAttribute("message", "登録しました");
        return "redirect:/todos";
    }

    @GetMapping("/todos/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "見つかりませんでした");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        return "edit";
    }

    @PostMapping("/todos/{id}/confirm")
    public String editConfirm(@PathVariable Long id, @Valid @ModelAttribute Todo todo,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            return "edit";
        }
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        return "edit-confirm";
    }

    @PostMapping("/todos/{id}/edit")
    public String editRewrite(@PathVariable Long id, @ModelAttribute Todo todo, Model model) {
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        return "edit";
    }

    @PostMapping("/todos/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Todo todo, RedirectAttributes redirectAttributes) {
        todo.setId(id);
        todoService.update(todo);
        redirectAttributes.addFlashAttribute("message", "保存しました");
        return "redirect:/todos";
    }

    @GetMapping("/todos/{id}/delete")
    public String deleteConfirm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            redirectAttributes.addFlashAttribute("message", "見つかりませんでした");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        model.addAttribute("id", id);
        return "delete";
    }

    @PostMapping("/todos/{id}/pin")
    public String togglePinned(@PathVariable Long id,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(name = "showCompleted", defaultValue = "false") boolean showCompleted,
            @RequestParam(defaultValue = "0") int trash,
            @RequestParam(defaultValue = "1") int page) {
        todoService.togglePinned(id);
        return "redirect:" + UriComponentsBuilder.fromPath("/todos")
                .queryParam("keyword", keyword)
                .queryParam("category", category)
                .queryParam("order", order)
                .queryParam("showCompleted", showCompleted)
                .queryParam("trash", trash)
                .queryParam("page", page)
                .build()
                .toUriString();
    }

    @PostMapping("/todos/{id}/restore")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.restore(id);
        redirectAttributes.addFlashAttribute("message", "Todoを戻しました");
        return "redirect:/todos?trash=1";
    }

    @PostMapping("/todos/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        todoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "削除しました");
        return "redirect:/todos";
    }
}
