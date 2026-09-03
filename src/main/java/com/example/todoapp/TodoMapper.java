package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoMapper {

    List<Todo> search(@Param("keyword") String keyword, @Param("category") String category,
            @Param("order") String order, @Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Todo> searchPage(@Param("keyword") String keyword, @Param("category") String category,
            @Param("order") String order, @Param("showCompleted") boolean showCompleted,
            @Param("trash") boolean trash, @Param("offset") int offset, @Param("limit") int limit);

    List<Todo> searchForExport(@Param("keyword") String keyword, @Param("category") String category,
            @Param("order") String order, @Param("showCompleted") boolean showCompleted,
            @Param("trash") boolean trash);

    int count(@Param("keyword") String keyword, @Param("category") String category,
            @Param("showCompleted") boolean showCompleted, @Param("trash") boolean trash);

    void insert(Todo todo);

    Todo findById(Long id);

    void update(Todo todo);

    void deleteById(Long id);

    void restoreById(Long id);

    void togglePinned(Long id);
}
