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

public abstract class EntityFinderImpl implements EntityFinder {

    private final Connection connection;
    private static final RuntimeException METHOD_FIELD_MATCHING_EXCEPTION = new IllegalStateException("No Method and Field is matching");


    EntityFinderImpl(JdbcTemplate jdbcTemplate) throws SQLException {
        this.connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
    }

    public <T> List<T> findAll(Class<? extends T> tClass) {
        return findAll(tClass, Condition.empty());
    }

    public <T> List<T> findAll(@NotNull Class<? extends T> tClass, @NotNull Condition condition) {
        validate(tClass);
        Entity entity = tClass.getAnnotation(Entity.class);
        List<Field> annotationsByType = Stream.of(tClass.getDeclaredFields()).toList();
        String merge = annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                map(elem -> getMerge(elem.getDeclaredAnnotation(OneToOne.class), elem.getType(), entity.alias())).
                collect(Collectors.joining());
        String selectItems = getColumnsSelect(annotationsByType, entity.alias());
        String order = String.join(", ", getOrder());
        try {
            PreparedStatement preparedStatement;
            if (!condition.getValues().isEmpty()) {
                if (!order.isEmpty()) {
                    preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s order by %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition(), order));
                } else {
                    preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition()));
                }
                for (int i = 0; i < condition.getValues().size(); i++) {
                    preparedStatement.setObject(i + 1, condition.getValues().get(i));
                }
            } else {
                preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s", selectItems, entity.table(), entity.alias(), merge));
            }
            List<T> map = mapFromAsList(preparedStatement.executeQuery(), entity, tClass);
            preparedStatement.close();
            return map;

        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public <T> List<T> findAll(Class<? extends T> tClass, Integer limit, Integer offset) {
        return findAll(tClass, Condition.empty(), limit, offset);
    }

    @Override
    public <T> List<T> findAll(Class<? extends T> tClass, Condition condition, Integer limit, Integer offset) {
        validate(tClass);
        Entity entity = tClass.getAnnotation(Entity.class);
        List<Field> annotationsByType = Stream.of(tClass.getDeclaredFields()).toList();
        String merge = annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                map(elem -> getMerge(elem.getDeclaredAnnotation(OneToOne.class), elem.getType(), entity.alias())).
                collect(Collectors.joining());
        String selectItems = getColumnsSelect(annotationsByType, entity.alias());
        String order = String.join(", ", getOrder());
        try {
            PreparedStatement preparedStatement;
            if (!condition.getValues().isEmpty()) {
                if (!order.isEmpty()) {
                    preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s order by %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition(), order, limit, offset));
                } else {
                    preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s where %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, condition.getCondition(), limit, offset));
                }
                for (int i = 0; i < condition.getValues().size(); i++) {
                    preparedStatement.setObject(i + 1, condition.getValues().get(i));
                }
            } else {
                if (!order.isEmpty()) {
                    preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s order by %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, order, limit, offset));
                } else {
                    preparedStatement = connection.prepareStatement(String.format("Select %s from %s as %s %s limit %s offset %s", selectItems, entity.table(), entity.alias(), merge, limit, offset));

                }
            }
            List<T> map = mapFromAsList(preparedStatement.executeQuery(), entity, tClass);
            preparedStatement.close();
            return map;

        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public <T> Integer count(Class<? extends T> tClass) {
        return count(tClass, Condition.empty());
    }

    @Override
    public <T> Integer count(Class<? extends T> tClass, Condition condition) {
        validate(tClass);
        Entity entity = tClass.getAnnotation(Entity.class);
        List<Field> annotationsByType = Stream.of(tClass.getDeclaredFields()).toList();
        String merge = annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                map(elem -> getMerge(elem.getDeclaredAnnotation(OneToOne.class), elem.getType(), entity.alias())).
                collect(Collectors.joining());
        String order = String.join(", ", getOrder());

        try {
            PreparedStatement preparedStatement;
            if (!condition.getValues().isEmpty()) {
                if (!order.isEmpty()) {
                    preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s where %s order by %s", entity.table(), entity.alias(), merge, condition.getCondition(), order));
                } else {
                    preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s where %s", entity.table(), entity.alias(), merge, condition.getCondition()));
                }
                for (int i = 0; i < condition.getValues().size(); i++) {
                    preparedStatement.setObject(i + 1, condition.getValues().get(i));
                }
            } else {
                if (!order.isEmpty()) {
                    preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s order by %s", entity.table(), entity.alias(), merge, order));

                } else {
                    preparedStatement = connection.prepareStatement(String.format("Select count(*) from %s as %s %s", entity.table(), entity.alias(), merge));
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            preparedStatement.close();
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private String capitalizeFirstLetter(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);

    }

    @Override
    public <T> void save(T value) {
        validate(value);
        Class<?> tClass = value.getClass();
        try {
            for (Field field : tClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(OneToOne.class)) {
                    save(tClass.getMethod("get" + capitalizeFirstLetter(field.getName())).invoke(value));
                }
            }
            List<Field> fields = Arrays.stream(tClass.getDeclaredFields()).filter(elem -> elem.isAnnotationPresent(EntityValue.class) || elem.isAnnotationPresent(OneToOne.class)).toList();
            StringJoiner joiner = new StringJoiner(",");
            for (Field _ : fields) {
                joiner.add("?");
            }
            String insert = "insert into " + tClass.getAnnotation(Entity.class).table() + " (" + getColumnsInsert(fields) + ") values (" + joiner + ")";

            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            insertSetStatement(fields, tClass, preparedStatement, value, 0);


            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void insertSetStatement(List<Field> fields, Class<?> tClass, PreparedStatement preparedStatement, T value, Integer startParameterIndex) throws InvocationTargetException, IllegalAccessException, SQLException {
        List<Method> getter = Arrays.stream(tClass.getDeclaredMethods())
                .filter(elem -> elem.getName().startsWith("get")).toList();
        for (int i = 0; i < fields.size(); i++) {
            final int temp = i;
            Method method = getter.stream().
                    filter(methodGet -> methodGet.getName().endsWith(capitalizeFirstLetter(fields.get(temp).getName())))
                    .findFirst()
                    .orElseThrow(() -> METHOD_FIELD_MATCHING_EXCEPTION);
            if (fields.get(temp).isAnnotationPresent(EntityValue.class) && fields.get(temp).getAnnotation(EntityValue.class).primary()) {
                preparedStatement.setObject(i + 1 + startParameterIndex, method.invoke(value));
            } else {
                //foreign key
                if (fields.get(temp).isAnnotationPresent(OneToOne.class)) {
                    Object invoke = method.invoke(value);
                    Field primaryField = Arrays.stream(invoke.getClass().getDeclaredFields()).filter(elem -> elem.isAnnotationPresent(EntityValue.class) && elem.getAnnotation(EntityValue.class).primary())
                            .findFirst()
                            .orElseThrow(() -> primaryKeyNptFoundException(invoke.getClass().getAnnotation(Entity.class)));
                    Method primaryMethod = Arrays.stream(primaryField.getDeclaringClass().getMethods())
                            .filter(methodForeign -> compareGet(methodForeign, primaryField))
                            .findFirst()
                            .orElseThrow(() -> METHOD_FIELD_MATCHING_EXCEPTION);
                    preparedStatement.setObject(i + 1 + startParameterIndex, primaryMethod.invoke(method.invoke(value)));
                } else {
                    preparedStatement.setObject(i + 1 + startParameterIndex, method.invoke(value));
                }

            }
        }
    }


    @Override
    public <T> void saveAll(List<T> values) {
        if (!values.isEmpty()) {
            validate(values);
            saveAll(getOtherInsertClassesAsList(values));
            List<Field> fields = Arrays.stream(values.getFirst().getClass().getDeclaredFields()).filter(elem -> elem.isAnnotationPresent(EntityValue.class) || elem.isAnnotationPresent(OneToOne.class)).toList();

            StringJoiner all = new StringJoiner(",");
            for (int i = 0; i < values.size(); i++) {
                StringJoiner parameter = new StringJoiner(",");
                for (Field _ : fields) {
                    parameter.add("?");
                }
                all.add("(" + parameter + ")");
            }

            String insert = "insert into " + values.getFirst().getClass().getAnnotation(Entity.class).table() + " (" + getColumnsInsert(fields) + ") values " + all;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insert);
                for (int i = 0; i < values.size(); i++) {
                    Class<?> tClass = values.get(i).getClass();
                    try {
                        insertSetStatement(fields, tClass, preparedStatement, values.get(i), fields.size() * i);
                    } catch (InvocationTargetException | IllegalAccessException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }

    @Override
    public <T> void update(T value) {
        validate(value);
        Arrays.stream(value.getClass().getDeclaredFields())
                .filter(elem -> elem.isAnnotationPresent(OneToOne.class)).forEach(field -> {
                    Method method1 = Arrays.stream(field.getDeclaringClass().getDeclaredMethods()).filter(method -> compareGet(method, field))
                            .findFirst()
                            .orElseThrow(() -> METHOD_FIELD_MATCHING_EXCEPTION);
                    try {
                        update(method1.invoke(value));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

        List<Field> fieldsEntityValue = new ArrayList<>(Arrays.stream(value.getClass().getDeclaredFields()).filter(elem -> elem.isAnnotationPresent(EntityValue.class)).toList());

        List<Method> methodsEntityValue = Arrays.stream(value.getClass().getDeclaredMethods())
                .filter(method -> fieldsEntityValue.stream()
                        .anyMatch(field -> compareGet(method, field)))
                .toList();

        String where = fieldsEntityValue.stream()
                .filter(field -> methodsEntityValue.stream()
                        .anyMatch(method -> field.getAnnotation(EntityValue.class).primary() && compareGet(method, field)))
                .map(elem -> elem.getAnnotation(EntityValue.class).value() + " = ?")
                .collect(Collectors.joining());

        String update = String.format("Update %s SET %s where %s", value.getClass().getAnnotation(Entity.class).table(), getUpdateSet(value), where);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            Optional<Method> primaryOptional = methodsEntityValue.stream()
                    .filter(method -> fieldsEntityValue.stream()
                            .anyMatch(field -> field.isAnnotationPresent(EntityValue.class) && field.getAnnotation(EntityValue.class).primary() && compareGet(method, field)))
                    .findFirst();
            Field[] declaredFields = value.getClass().getDeclaredFields();
            int index = 0;
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(EntityValue.class) || declaredField.isAnnotationPresent(OneToOne.class)) {
                    Method[] methodsAll = value.getClass().getDeclaredMethods();
                    for (Method method : methodsAll) {
                        if (compareGet(method, declaredField)) {
                            //search for primary key
                            if (declaredField.isAnnotationPresent(OneToOne.class)) {
                                Object foreign = method.invoke(value);
                                Method[] declaredMethods = foreign.getClass().getDeclaredMethods();
                                Field field = Arrays.stream(foreign.getClass().getDeclaredFields()).filter(elem -> elem.isAnnotationPresent(EntityValue.class) && elem.getAnnotation(EntityValue.class).primary())
                                        .findFirst()
                                        .orElseThrow(() -> primaryKeyNptFoundException(foreign.getClass().getAnnotation(Entity.class)));
                                for (Method declaredMethod : declaredMethods) {
                                    if (compareGet(declaredMethod, field)) {
                                        preparedStatement.setObject(1 + index++, declaredMethod.invoke(foreign));
                                    }
                                }
                            } else {
                                preparedStatement.setObject(1 + index++, method.invoke(value));
                            }
                        }

                    }
                }
            }


            if (primaryOptional.isPresent()) {
                preparedStatement.setObject(1 + index++, primaryOptional.get().invoke(value));
            }

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public <T> void delete(T value) {
        validate(value);
        String table = value.getClass().getAnnotation(Entity.class).table();
        Field fieldPrimary = Arrays.stream(value.getClass().getDeclaredFields()).
                filter(field -> field.isAnnotationPresent(EntityValue.class) && field.getAnnotation(EntityValue.class).primary())
                .findFirst()
                .orElseThrow(() -> METHOD_FIELD_MATCHING_EXCEPTION);
        Method methodPrimary = Arrays.stream(value.getClass().getDeclaredMethods())
                .filter(method -> compareGet(method, fieldPrimary))
                .findFirst()
                .orElseThrow(() -> METHOD_FIELD_MATCHING_EXCEPTION);
        try {
            String delete = String.format("DELETE FROM %s where %s = %s", table, fieldPrimary.getName(), methodPrimary.invoke(value));
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            Arrays.stream(value.getClass().getDeclaredFields()).filter(elem -> elem.isAnnotationPresent(OneToOne.class)).forEach(field -> {
                Method method1 = Arrays.stream(value.getClass().getMethods()).filter(method -> compareGet(method, field))
                        .findFirst()
                        .orElseThrow(() -> METHOD_FIELD_MATCHING_EXCEPTION);
                try {
                    delete(method1.invoke(value));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean compareGet(Method method, Field field) {
        return method.getName().equals("get" + capitalizeFirstLetter(field.getName()));
    }

    private <T> String getUpdateSet(T value) {
        StringJoiner joiner = new StringJoiner(", ");
        List<Field> list = Arrays.stream(value.getClass().getDeclaredFields()).toList();
        list.stream().
                filter(elem -> elem.isAnnotationPresent(EntityValue.class)).
                map(elem -> elem.getDeclaredAnnotation(EntityValue.class)).
                forEach(elem -> joiner.add(elem.value() + " = ?"));

        //other classes
        list.stream().
                filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                map(elem -> elem.getDeclaredAnnotation(OneToOne.class)).
                forEach(elem -> joiner.add(elem.foreignKey() + " = ?"));

        return joiner.toString();
    }

    protected abstract List<String> getOrder();

    /**
     * @param values references values
     * @param <T>    the type of the current/referenced class value
     * @return the referenced objects
     */
    private <T> List<Object> getOtherInsertClassesAsList(List<T> values) {
        List<Object> foreignList = new ArrayList<>();
        values.stream().map(value -> {
            List<Field> fieldsIndex = Arrays.stream(value.getClass().getDeclaredFields())
                    .filter(elem -> elem.isAnnotationPresent(EntityValue.class) || elem.isAnnotationPresent(OneToOne.class)).toList();

            return fieldsIndex.stream()
                    .filter(field -> field.isAnnotationPresent(OneToOne.class))
                    .map(field -> Arrays.stream(field.getDeclaringClass().getMethods())
                            .filter(method -> compareGet(method, field)).findFirst()
                            .orElseThrow(() -> METHOD_FIELD_MATCHING_EXCEPTION))
                    .map(method -> {
                        try {
                            return method.invoke(value);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }).findFirst();

        }).forEach(elem -> elem.ifPresent(foreignList::add));
        return foreignList;
    }


    private String getColumnsInsert(List<Field> annotationsByType) {
        StringJoiner joiner = new StringJoiner(", ");
        annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(EntityValue.class)).
                map(elem -> elem.getDeclaredAnnotation(EntityValue.class)).
                forEach(elem -> joiner.add(elem.value()));

        //other classes
        annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                map(elem -> elem.getDeclaredAnnotation(OneToOne.class)).
                forEach(elem -> joiner.add(elem.foreignKey()));

        return joiner.toString();
    }

    private String getColumnsSelect(List<Field> annotationsByType, String placeholder) {
        StringJoiner joiner = new StringJoiner(", ");
        annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(EntityValue.class)).
                map(elem -> elem.getDeclaredAnnotation(EntityValue.class)).
                forEach(elem -> joiner.add(placeholder + "." + elem.value()));

        //other classes
        annotationsByType.stream().
                filter(elem -> elem.isAnnotationPresent(OneToOne.class)).
                map(Field::getType).
                filter(elem -> elem.isAnnotationPresent(Entity.class)).
                map(elem -> Pair.of(Arrays.stream(elem.getDeclaredFields()).toList(), elem.getAnnotation(Entity.class).alias())).
                forEach(elem -> joiner.add(getColumnsSelect(elem.getFirst(), elem.getSecond())));

        return joiner.toString();
    }

    private <T> String getMerge(OneToOne oneToOne, Class<T> aClass, String primaryAlias) {
        if (aClass.isAnnotationPresent(Entity.class)) {
            Entity entity = aClass.getAnnotation(Entity.class);
            return String.format("LEFT JOIN %s as %s ON %s.%s = %s.%s", entity.table(), entity.alias(), primaryAlias, oneToOne.foreignKey(), entity.alias(), oneToOne.primaryKey());
        }

        throw new IllegalStateException("Annotation Entity not found for " + aClass.getName());
    }

    /**
     * @param array  setter
     * @param values annotations of attribute
     * @return array and values in same order, setter position must be synchronized, which is not warranted by default
     */
    private Method[] order(Method[] array, EntityValue[] values) {
        for (int i = 0; i < Math.min(array.length, values.length); i++) {
            //nicht gleich bei index i
            if (!array[i].getName().toLowerCase().endsWith(values[i].value().toLowerCase())) {
                for (int k = i; k < array.length; k++) {
                    if (array[k].getName().toLowerCase().endsWith(values[i].value().toLowerCase())) {
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
     * @param resultSet Set from Databse
     * @param tClass    the class to map
     * @param entity    to get alias
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
            Method[] declaredMethods = order(
                    Arrays.stream(tClass.getClass().getDeclaredMethods())
                            .filter(elem -> elem.getName().startsWith("set")).toArray(Method[]::new),
                    entityValueList.toArray(EntityValue[]::new)
            );
            for (int i = 0; i < Math.min(declaredMethods.length, entityValueList.size()); i++) {
                String format = String.format("%s.%s", entity.alias(), entityValueList.get(i).value());
                switch (entityValueList.get(i).type()) {
                    case INT -> declaredMethods[i].invoke(tClass, resultSet.getInt(format));
                    case VARCHAR -> declaredMethods[i].invoke(tClass, resultSet.getString(format));
                    case BIG_DECIMAL -> declaredMethods[i].invoke(tClass, resultSet.getBigDecimal(format));
                    case BOOLEAN -> declaredMethods[i].invoke(tClass, resultSet.getBoolean(format));
                    case TIME -> declaredMethods[i].invoke(tClass, resultSet.getTime(format));
                    case TIMESTAMP -> declaredMethods[i].invoke(tClass, resultSet.getTimestamp(format));
                }
            }
            for (Class<?> aClass1 : oneToOneClass) {
                Entity annotation = aClass1.getAnnotation(Entity.class);
                Object other = mapFrom(resultSet, aClass1.getDeclaredConstructor().newInstance(), annotation);
                Optional<Method> first = Arrays.stream(declaredMethods).filter(method -> method.getName().equals("set" + annotation.table())).findFirst();
                Method method = first.orElseThrow(() -> new IllegalStateException(String.format("Setter set%s not found", annotation.table())));
                method.invoke(tClass, other);
            }
            return tClass;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @org.jetbrains.annotations.NotNull
    private <T> List<T> mapFromAsList(ResultSet resultSet, Entity entity, Class<? extends T> tClass) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            T value = mapFrom(resultSet, tClass.getDeclaredConstructor().newInstance(), entity);
            result.add(value);
        }
        return result;
    }

    private static IllegalStateException primaryKeyNptFoundException(Entity entity){
        return new IllegalStateException(String.format("Primary key for Entity %s not found", entity.table()));
    }

    @Override
    public <T> void validate(T entity) {
        validate(entity.getClass());
    }

    @Override
    public <T> void validate(List<T> entity) {
        entity.forEach(this::validate);
    }

    @Override
    public <T> void validate(Class<T> entity) {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Annotation Entity not found for " + entity.getName());
        }

        Optional<Field> primary = Arrays.stream(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(EntityValue.class) && field.getAnnotation(EntityValue.class).primary()).findFirst();
        if (primary.isEmpty()) {
            throw new IllegalStateException("Primary key not found for " + entity.getName());
        }
    }
}
