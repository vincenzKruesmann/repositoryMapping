package de.kruesmann.repositorymapping.database;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class EntityFinderNoOrder extends EntityFinderImpl{
    public EntityFinderNoOrder(JdbcTemplate jdbcTemplate) throws SQLException {
        super(jdbcTemplate);
    }

    @Override
    protected List<String> getOrder() {
        return List.of();
    }
}
