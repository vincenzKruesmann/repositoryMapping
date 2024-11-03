package de.kruesmann.repositorymapping.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityValue {
    String value();
    Sql.Types type() default Sql.Types.VARCHAR;
    boolean primary() default false;
}
