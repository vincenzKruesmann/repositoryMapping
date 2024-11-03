package de.kruesmann.repositorymapping.database;

import java.util.List;

public interface ReadFinder {
    /**
     *
     * @param tClass the class
     * @return all entities of type T
     * @param <T> the type
     */
    <T> List<T> findAll(Class<? extends T> tClass);

    /**
     *
     * @param tClass the class
     * @param condition the where clause
     * @return all entities of type T with a condition
     * @param <T> the type
     */
    <T> List<T> findAll(Class<? extends T> tClass, Condition condition);
    /**
     *
     * @param tClass the class
     * @param limit the maximum data to be retrieved
     * @param offset the starting index of the database
     * @return all entities of type T
     * @param <T> the type
     */
    <T> List<T> findAll(Class<? extends T> tClass, Integer limit, Integer offset);

    /**
     *
     * @param tClass the class
     * @param condition the where clause
     * @param limit the maximum data to be retrieved
     * @param offset the starting index of the database
     * @return all entities of type T
     * @param <T> the type
     */
    <T> List<T> findAll(Class<? extends T> tClass, Condition condition, Integer limit, Integer offset);

    /**
     *
     * @param tClass the class
     * @return the count of all entities of type T
     * @param <T> the type
     */
    <T> Integer count(Class<? extends T> tClass);

    /**
     *
     * @param tClass the class
     * @param condition the where clause
     * @return the count of all entities of type T
     * @param <T> the type
     */
    <T> Integer count(Class<? extends T> tClass, Condition condition);
}
