package com.example.jwt_springsecurity.service;


import com.example.jwt_springsecurity.domain.Member;
import com.example.jwt_springsecurity.domain.Todo;
import com.example.jwt_springsecurity.dto.PageResponseDto;
import com.example.jwt_springsecurity.dto.TodoDto;
import com.example.jwt_springsecurity.dto.TodoListDto;
import com.example.jwt_springsecurity.exception.CustomException;
import com.example.jwt_springsecurity.exception.ErrorCode;
import com.example.jwt_springsecurity.repository.MemberRepository;
import com.example.jwt_springsecurity.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.jwt_springsecurity.config.SecurityUtil.getCurrentMemberId;


@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public TodoDto createTodo(TodoDto todoDto) {

        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        Todo todo = Todo.builder()
                .title(todoDto.getTitle())
                .completed(false)
                .member(member)
                .build();

        todoRepository.save(todo);
        return todoDto;
    }

    @Transactional(readOnly = true)
    public PageResponseDto findMyTODO(int pageNo, int pageSize, String sortBy) {

        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        if (member.getTodoList().isEmpty()) {
            throw new CustomException(ErrorCode.TODO_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        Page<Todo> todoPage = todoRepository.findAllByMember(pageable, member);

        List<Todo> todoList = todoPage.getContent();

        List<TodoListDto> content = todoList.stream()
                .map(todo -> TodoListDto.builder()
                        .id(todo.getId())
                        .title(todo.getTitle())
                        .completed(todo.getCompleted())
                        .build())
                .collect(Collectors.toList());


        return PageResponseDto.builder()
                .content(content)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(todoPage.getTotalElements())
                .totalPages(todoPage.getTotalPages())
                .last(todoPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponseDto findAll(int pageNo, int pageSize, String sortBy) {

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        Page<Todo> todoPage = todoRepository.findAll(pageable);

        List<Todo> todoList = todoPage.getContent();

        List<TodoListDto> content = todoList.stream()
                .map(todo -> TodoListDto.builder()
                        .id(todo.getId())
                        .title(todo.getTitle())
                        .completed(todo.getCompleted())
                        .build())
                .collect(Collectors.toList());


        return PageResponseDto.builder()
                .content(content)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(todoPage.getTotalElements())
                .totalPages(todoPage.getTotalPages())
                .last(todoPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public TodoListDto findTodoById(Long id) {

        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        Todo todo = todoRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        return TodoListDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }

    @Transactional
    public TodoDto updateTodoById(Long id, TodoDto todoDto) {

        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        Todo todo = todoRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todo.setTitle(todoDto.getTitle());

        return TodoDto.builder()
                .title(todo.getTitle())
                .build();
    }

    @Transactional
    public void deleteTodoById(Long id) {

        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        Todo todo = todoRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todoRepository.delete(todo);
    }

    @Transactional
    public TodoListDto complete(Long id) {

        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        Todo todo = todoRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todo.setCompleted(true);

        return TodoListDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }
}
