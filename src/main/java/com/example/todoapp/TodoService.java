package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TodoService {

    private final TodoMapper todoMapper;
    private final ExperienceMapper experienceMapper;

    public TodoService(TodoMapper todoMapper, ExperienceMapper experienceMapper) {
        this.todoMapper = todoMapper;
        this.experienceMapper = experienceMapper;
    }

    public List<Todo> search(String keyword, String category, String order) {
        return search(keyword, category, order, null, null);
    }

    public List<Todo> search(String keyword, String category, String order, LocalDate from, LocalDate to) {
        return todoMapper.search(keyword, category, order, from, to);
    }

    public List<Todo> searchPage(String keyword, String category, String order, boolean showCompleted,
            boolean trash, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return todoMapper.searchPage(keyword, category, order, showCompleted, trash, offset, pageSize);
    }

    public List<Todo> searchForExport(String keyword, String category, String order,
            boolean showCompleted, boolean trash) {
        return todoMapper.searchForExport(keyword, category, order, showCompleted, trash);
    }

    public int count(String keyword, String category, boolean showCompleted, boolean trash) {
        return todoMapper.count(keyword, category, showCompleted, trash);
    }

    public Todo findById(Long id) {
        return todoMapper.findById(id);
    }

    public void create(Todo todo) {
        todoMapper.insert(todo);
        log.info("Todoの登録が正常に完了しました。 id={}", todo.getId());
    }

    @Transactional
    public void update(Todo todo) {
        Todo existingTodo = todoMapper.findById(todo.getId());
        todoMapper.update(todo);
        if (existingTodo != null
                && !Boolean.TRUE.equals(existingTodo.getCompleted())
                && Boolean.TRUE.equals(todo.getCompleted())) {
            experienceMapper.addPoints(experiencePointsFor(todo.getPriority()));
        }
        log.info("Todoの編集が正常に完了しました。 id={}", todo.getId());
    }

    public int getExperiencePoints() {
        return experienceMapper.getTotalPoints();
    }

    private int experiencePointsFor(Integer priority) {
        return switch (priority) {
            case 1 -> 30;
            case 2 -> 20;
            case 3 -> 10;
            default -> throw new IllegalArgumentException("priority must be 1, 2, or 3");
        };
    }

    public void togglePinned(Long id) {
        todoMapper.togglePinned(id);
    }

    public void restore(Long id) {
        todoMapper.restoreById(id);
    }

    public void delete(Long id) {
        todoMapper.deleteById(id);
        log.info("Todoの削除が正常に完了しました。 id={}", id);
    }
}
