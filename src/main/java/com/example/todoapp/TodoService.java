package com.example.todoapp;

import java.util.List;

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
        return todoMapper.search(keyword, category, order);
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
