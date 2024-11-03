package de.kruesmann.repositorymapping.database;

import java.util.List;

public interface WriteFinder {
    /**
     *
     * @param value to save
     * @param <T> the type of entity
     */
    <T> void save(T value);

    /**
     *
     * @param values to save
     * @param <T> the type of the Entities
     */
    <T> void saveAll(List<T> values);

    /**
     * Updates also subclasses
     * @param value tu update
     * @param <T> the type of the entity to update
     */
    <T> void update(T value);

    /**
     *
     * @param value entity to delete, identified by the primary key of {@link de.kruesmann.repositorymapping.entities.Entity}
     * @param <T> the type of the object
     */
    <T> void delete(T value);

}
