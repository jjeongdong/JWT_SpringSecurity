package com.example.jwt_springsecurity.repository;

import com.example.jwt_springsecurity.domain.Member;
import com.example.jwt_springsecurity.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findAllByMember(Pageable pageable, Member member);

    Optional<Todo> findByIdAndMember(Long id, Member member);
}
