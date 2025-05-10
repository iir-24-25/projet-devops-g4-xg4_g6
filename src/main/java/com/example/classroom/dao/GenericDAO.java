package com.example.classroom.dao;

import java.util.List;

public interface GenericDAO<T> {
    void create(T entity);
    T findById(int id);
    List<T> findAll();
    void update(T entity);
    void delete(int id);
}
