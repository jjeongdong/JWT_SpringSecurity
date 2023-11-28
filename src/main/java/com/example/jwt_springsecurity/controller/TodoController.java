package com.example.jwt_springsecurity.controller;

import com.example.jwt_springsecurity.dto.PageResponseDto;
import com.example.jwt_springsecurity.dto.TodoDto;
import com.example.jwt_springsecurity.dto.TodoListDto;
import com.example.jwt_springsecurity.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/list")
    public PageResponseDto list(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            Authentication authentication
    ) {
        return todoService.findAll(pageNo, pageSize, sortBy, authentication);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoListDto> getTodoById(@PathVariable Long id, Authentication authentication) {
        TodoListDto todoListDto = todoService.findTodoById(id, authentication);
        return ResponseEntity.ok(todoListDto);
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody @Valid TodoDto todoDto, Authentication authentication) {
        return ResponseEntity.ok(todoService.createTodo(todoDto, authentication));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodoById(@PathVariable Long id, @Valid @RequestBody TodoDto todoDto,Authentication authentication) {
        TodoDto updatedTodo = todoService.updateTodoById(id, todoDto, authentication);
        return ResponseEntity.ok(updatedTodo);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable Long id, Authentication authentication) {
        todoService.deleteTodoById(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<TodoListDto> complete(@PathVariable Long id, Authentication authentication) {
        TodoListDto todoListDto = todoService.complete(id, authentication);
        return ResponseEntity.ok(todoListDto);
    }


}
