package de.kruesmann.repositorymapping.entities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EntityValue {
    String value();
    Sql.Types type() default Sql.Types.VARCHAR;
}
