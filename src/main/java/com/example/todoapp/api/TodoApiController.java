package com.example.todoapp.api;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.URI;
import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;

@RestController
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/api/todos")
    public List<TodoDto> todos(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String order,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return todoService.search(keyword, category, order, from, to).stream()
                .map(TodoDto::from)
                .toList();
    }

    @GetMapping(value = "/api/todos.csv", produces = "text/csv")
    public ResponseEntity<byte[]> todosCsv(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(name = "showCompleted", defaultValue = "false") boolean showCompleted,
            @RequestParam(defaultValue = "0") int trash) {
        if (!"desc".equals(order)) {
            order = "asc";
        }
        List<Todo> todos = todoService.searchForExport(keyword, category, order,
                showCompleted, trash == 1);

        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("やること,ジャンル,優先度,期限,状態,メモ\r\n");
        for (Todo todo : todos) {
            csv.append(csvCell(todo.getTitle())).append(',')
                    .append(csvCell(todo.getCategory())).append(',')
                    .append(csvCell(priorityLabel(todo.getPriority()))).append(',')
                    .append(csvCell(todo.getDueDate())).append(',')
                    .append(csvCell(statusLabel(todo))).append(',')
                    .append(csvCell(todo.getDetail())).append("\r\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8));
        headers.setContentDispositionFormData("attachment", "todos.csv");
        return new ResponseEntity<>(csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                headers, HttpStatus.OK);
    }

    private String priorityLabel(Integer priority) {
        return priority == 1 ? "高" : (priority == 2 ? "中" : "低");
    }

    private String statusLabel(Todo todo) {
        String status = Boolean.TRUE.equals(todo.getCompleted()) ? "完了" : "未完了";
        return todo.getCompletedAt() == null ? status : status + " (" + todo.getCompletedAt() + ")";
    }

    private String csvCell(Object value) {
        if (value == null) {
            return "";
        }
        String text = value.toString();
        if (text.startsWith("=") || text.startsWith("+")) {
            text = "'" + text;
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    @GetMapping("/api/todos/{id}")
    public ResponseEntity<?> todo(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return notFound(id);
        }
        return ResponseEntity.ok(TodoDto.from(todo));
    }

    @PostMapping("/api/todos")
    public ResponseEntity<TodoDto> create(@Valid @RequestBody TodoRequest request) {
        Todo todo = request.toTodo();
        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }
        todoService.create(todo);
        Todo createdTodo = todoService.findById(todo.getId());
        return ResponseEntity.created(URI.create("/api/todos/" + createdTodo.getId()))
                .body(TodoDto.from(createdTodo));
    }

    @PutMapping("/api/todos/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            return notFound(id);
        }
        Todo todo = request.toTodo();
        todo.setId(id);
        todoService.update(todo);
        return ResponseEntity.ok(TodoDto.from(todoService.findById(id)));
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            return notFound(id);
        }
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ProblemDetail> notFound(Long id) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setType(URI.create("/errors/todo-not-found"));
        problem.setTitle("Todo not found");
        problem.setDetail("Todo with id " + id + " was not found.");
        problem.setInstance(URI.create("/api/todos/" + id));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ProblemDetail> badRequest(MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("about:blank"));
        problem.setTitle("Bad Request");
        problem.setDetail("入力に誤りがあります");
        problem.setInstance(URI.create(request.getRequestURI()));

        List<Map<String, String>> errors = new ArrayList<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("field", fieldError.getField());
            error.put("message", fieldError.getDefaultMessage());
            errors.add(error);
        }
        problem.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
