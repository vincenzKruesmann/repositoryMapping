package de.kruesmann.repositorymapping.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class EntityFinderNoOrder extends EntityFinderImpl{
    EntityFinderNoOrder(JdbcTemplate jdbcTemplate) throws SQLException {
        super(jdbcTemplate);
    }

    @Override
    protected List<String> getOrder() {
        return List.of();
    }
}
