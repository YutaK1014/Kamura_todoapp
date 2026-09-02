package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TodoService {

    private final TodoMapper todoMapper;

    public TodoService(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    public List<Todo> search(String keyword, String category, String order) {
        return search(keyword, category, order, null, null);
    }

    public List<Todo> search(String keyword, String category, String order, LocalDate from, LocalDate to) {
        return todoMapper.search(keyword, category, order, from, to);
    }

    public List<Todo> searchPage(String keyword, String category, String order, boolean showCompleted,
            int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return todoMapper.searchPage(keyword, category, order, showCompleted, offset, pageSize);
    }

    public int count(String keyword, String category, boolean showCompleted) {
        return todoMapper.count(keyword, category, showCompleted);
    }

    public Todo findById(Long id) {
        return todoMapper.findById(id);
    }

    public void create(Todo todo) {
        todoMapper.insert(todo);
        log.info("Todoの登録が正常に完了しました。 id={}", todo.getId());
    }

    public void update(Todo todo) {
        todoMapper.update(todo);
        log.info("Todoの編集が正常に完了しました。 id={}", todo.getId());
    }

    public void delete(Long id) {
        todoMapper.deleteById(id);
        log.info("Todoの削除が正常に完了しました。 id={}", id);
    }
}
