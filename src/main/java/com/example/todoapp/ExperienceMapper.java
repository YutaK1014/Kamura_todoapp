package com.example.todoapp;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExperienceMapper {
    int getTotalPoints();
    void addPoints(int points);
}
