package de.kruesmann.repositorymapping.database;

import de.kruesmann.repositorymapping.entities.Entity;
import de.kruesmann.repositorymapping.entities.EntityValue;
import de.kruesmann.repositorymapping.entities.OneToOne;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EntityFinderImpl implements EntityFinder{

    private final Connection connection;

    EntityFinderImpl(JdbcTemplate jdbcTemplate) throws SQLException {
        this.connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
    }

    public <T>List<T> findAll(Class<? extends T> tClass) {
        return findAll(tClass, Condition.empty());
    }

    public <T>List<T> findAll(@NotNull Class<? extends T> tClass, @NotNull Condition condition) {
        Entity entity = tClass.getAnnotation(Entity.class);
        if(entity != null) {
            List<Field> annotationsByType = Stream.of(tClass.getDeclaredFields()).toList();
            String merge = annotationsByType.stream().
                    filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                    map(elem -> getMerge(elem.getDeclaredAnnotation(OneToOne.class), elem.getType(), entity.alias())).
                    collect(Collectors.joining());
            String selectItems  = getColumns(annotationsByType, entity.alias());
            String order = String.join(", ", getOrder());
            try {
                PreparedStatement preparedStatement;
                if(!condition.getValues().isEmpty()){
                    if(!order.isEmpty()){
                        preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s order by %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition(), order));
                    }else {
                        preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition()));
                    }
                    for (int i = 0; i < condition.getValues().size(); i++) {
                        preparedStatement.setObject(i+1, condition.getValues().get(i));
                    }
                }else {
                    preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s", selectItems, entity.table(), entity.alias(), merge));
                }

                return mapFromAsList(preparedStatement.executeQuery(), tClass.getDeclaredConstructor().newInstance(), entity);

            }catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e){
                throw new RuntimeException(e);
            }

        }
        throw new IllegalStateException(String.format("Entity %s not found", tClass.getName()));
    }

    @Override
    public <T> List<T> findAll(Class<? extends T> tClass, Integer limit, Integer offset) {
        return findAll(tClass, Condition.empty(), limit, offset);
    }

    @Override
    public <T> List<T> findAll(Class<? extends T> tClass, Condition condition, Integer limit, Integer  offset) {
        Entity entity = tClass.getAnnotation(Entity.class);
        if(entity != null) {
            List<Field> annotationsByType = Stream.of(tClass.getDeclaredFields()).toList();
            String merge = annotationsByType.stream().
                    filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                    map(elem -> getMerge(elem.getDeclaredAnnotation(OneToOne.class), elem.getType(), entity.alias())).
                    collect(Collectors.joining());
            String selectItems  = getColumns(annotationsByType, entity.alias());
            String order = String.join(", ", getOrder());
            try {
                PreparedStatement preparedStatement;
                if(!condition.getValues().isEmpty()){
                    if(!order.isEmpty()){
                        preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s order by %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition(), order, limit, offset));
                    }else {
                        preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition(), limit, offset));
                    }
                    for (int i = 0; i < condition.getValues().size(); i++) {
                        preparedStatement.setObject(i+1, condition.getValues().get(i));
                    }
                }else {
                    if(!order.isEmpty()){
                        preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s order by %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, order, limit, offset));
                    }else {
                        preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, limit, offset));

                    }
                }

                return mapFromAsList(preparedStatement.executeQuery(), tClass.getDeclaredConstructor().newInstance(), entity);

            }catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e){
                throw new RuntimeException(e);
            }

        }
        throw new IllegalStateException(String.format("Entity %s not found", tClass.getName()));    }


    @Override
    public <T> Integer count(Class<? extends T> tClass) {
        return count(tClass, Condition.empty());
    }

    @Override
    public <T> Integer count(Class<? extends T> tClass, Condition condition) {
        Entity entity = tClass.getAnnotation(Entity.class);
        if(entity != null) {
            List<Field> annotationsByType = Stream.of(tClass.getDeclaredFields()).toList();
            String merge = annotationsByType.stream().
                    filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                    map(elem -> getMerge(elem.getDeclaredAnnotation(OneToOne.class), elem.getType(), entity.alias())).
                    collect(Collectors.joining());
            String order = String.join(", ", getOrder());

            try{
                PreparedStatement preparedStatement;
                if(!condition.getValues().isEmpty()){
                    if(!order.isEmpty()){
                        preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s where %s order by %s", entity.table(), entity.alias(), merge, condition.getCondition(), order));
                    }else {
                        preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s where %s", entity.table(), entity.alias(), merge, condition.getCondition()));
                    }
                    for (int i = 0; i < condition.getValues().size(); i++) {
                        preparedStatement.setObject(i+1, condition.getValues().get(i));
                    }
                }else {
                    if(!order.isEmpty()){
                        preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s order by %s", entity.table(), entity.alias(), merge, order));

                    }else {
                        preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s", entity.table(), entity.alias(), merge));
                    }
                }
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        throw new IllegalStateException(String.format("Entity %s not found", tClass.getName()));


    }

    protected abstract List<String> getOrder();

    private String getColumns(List<Field> annotationsByType, String placeholder) {
        StringJoiner joiner = new StringJoiner(", ");
        annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(EntityValue.class)).
                map(elem -> elem.getDeclaredAnnotation(EntityValue.class)).
        forEach(elem -> joiner.add(placeholder + "." +elem.value()));

        //other classes
        annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                map(Field::getType).
                filter(elem -> elem.isAnnotationPresent(Entity.class)).
                map(elem -> Pair.of(Arrays.stream(elem.getDeclaredFields()).toList(), elem.getAnnotation(Entity.class).alias())).
                forEach(elem -> joiner.add(getColumns(elem.getFirst(), elem.getSecond())));

        return joiner.toString();
    }

    private <T> String getMerge(OneToOne oneToOne, Class<T> aClass, String primaryAlias) {
        if(aClass.isAnnotationPresent(Entity.class)){
            Entity entity = aClass.getAnnotation(Entity.class);
            return String.format("INNER JOIN %s as %s ON %s.%s = %s.%s", aClass.getSimpleName(), entity.alias(), primaryAlias, oneToOne.foreignKey(), entity.alias(),oneToOne.primaryKey());
        }

        throw new IllegalStateException("Annotation Entity not found for " + aClass.getName());
    }

    /**
     *
     * @param array setter
     * @param values annotations of attribute
     * @return array and values in same order, setter position must be synchronized, which is not warranted by default
     */
    private Method[] order(Method[] array, EntityValue[] values){
        for (int i = 0; i < Math.min(array.length, values.length); i++) {
            //nicht gleich bei index i
            if(!array[i].getName().toLowerCase().endsWith(values[i].value().toLowerCase())) {
                for (int k = i; k <array.length; k++) {
                    if(array[k].getName().toLowerCase().endsWith(values[i].value().toLowerCase())) {
                        Method old = array[i];
                        array[i] = array[k];
                        array[k] = old;
                    }
                }
            }
        }

        return array;
    }

    /**
     *
     * @param resultSet Set from Databse
     * @param tClass the class to map
     * @param entity to get alias
     * @return parsed Object
     */
    private <T> T mapFrom(ResultSet resultSet, @NotNull T tClass, Entity entity) {
        try {
            List<Field> annotationsByType = Stream.of(tClass.getClass().getDeclaredFields()).toList();
            List<EntityValue> entityValueList = annotationsByType.stream().
                    filter(elem -> elem.isAnnotationPresent(EntityValue.class)).
                    map(elem -> elem.getDeclaredAnnotation(EntityValue.class)).toList();


            List<? extends Class<?>> oneToOneClass = annotationsByType.stream().
                    filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                    map(Field::getType).toList();

            //entityValue and Methods synchronized by index
            Method[] declaredFields = order(
                    Arrays.stream(tClass.getClass().getDeclaredMethods())
                            .filter(elem -> elem.getName().startsWith("set")).toArray(Method[]::new),
                    entityValueList.toArray(EntityValue[]::new)
            );
            for (int i = 0; i < Math.min(declaredFields.length, entityValueList.size()); i++) {
                    String format = String.format("%s.%s", entity.alias(), entityValueList.get(i).value());
                    switch (entityValueList.get(i).type()) {
                        case INT -> declaredFields[i].invoke(tClass, resultSet.getInt(format));
                        case VARCHAR -> declaredFields[i].invoke(tClass, resultSet.getString(format));
                        case BIG_DECIMAL -> declaredFields[i].invoke(tClass, resultSet.getBigDecimal(format));
                        case BOOLEAN -> declaredFields[i].invoke(tClass, resultSet.getBoolean(format));
                        case TIME -> declaredFields[i].invoke(tClass, resultSet.getTime(format));
                        case TIMESTAMP -> declaredFields[i].invoke(tClass, resultSet.getTimestamp(format));
                    }
            }
            for (Class<?> aClass1 : oneToOneClass) {
                Object other = mapFrom(resultSet, aClass1.getDeclaredConstructor().newInstance(), aClass1.getAnnotation(Entity.class));
                //set entityValueList
                Optional<Method> first = Arrays.stream(declaredFields).filter(elem -> elem.getName().equals("set" + aClass1.getSimpleName())).findFirst();
                Method method = first.orElseThrow();
                method.invoke(tClass, other);
            }
            return tClass;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @org.jetbrains.annotations.NotNull
    private <T> List<T> mapFromAsList(ResultSet resultSet, @NotNull T tClass, Entity entity) throws SQLException {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(mapFrom(resultSet, tClass, entity));
        }
        return result;
    }
        
}
