package org.company.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.company.configuration.Configuration;
import org.company.context.ApplicationContext;
import org.company.context.Bean;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource implements Bean {

    private HikariDataSource hikariDataSource;

    @Override
    public void init(ApplicationContext context) {
        final var configuration = context.getBean(Configuration.class);
        final var hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(configuration.getDb().getUrl());
        hikariConfig.setUsername(configuration.getDb().getUsername());
        hikariConfig.setPassword(configuration.getDb().getPassword());
        hikariConfig.setMaximumPoolSize(configuration.getWorkerCount());

        hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public void close() {
        hikariDataSource.close();
    }
}
