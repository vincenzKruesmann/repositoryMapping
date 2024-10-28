package de.kruesmann.repositorymapping.database;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public interface EntityFinder {
    <T> List<T> findAll(Class<? extends T> tClass);
    <T> List<T> findAll(Class<? extends T> tClass, Condition condition);
    <T> List<T> findAll(Class<? extends T> tClass, Integer limit, Integer offset);
    <T> List<T> findAll(Class<? extends T> tClass, Condition condition, Integer limit, Integer offset);

    <T> Integer count(Class<? extends T> tClass);
    <T> Integer count(Class<? extends T> tClass, Condition condition);

}
