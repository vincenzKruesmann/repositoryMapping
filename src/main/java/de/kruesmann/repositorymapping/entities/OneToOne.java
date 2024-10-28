package de.kruesmann.repositorymapping.entities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {
    String foreignKey();
    String primaryKey();
}
