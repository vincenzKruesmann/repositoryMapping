package de.kruesmann.repositorymapping.database;

import java.util.List;

public interface EntityFinder extends ReadFinder, WriteFinder {
    <T> void validate(T entity);
    <T> void validate(List<T> entity);
    <T> void validate(Class<T> entity);
}
