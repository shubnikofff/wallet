package org.company.repository;

import org.company.model.Transaction;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class TransactionRepository implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    private DataSource dataSource;

    @Override
    public void init(ApplicationContext context) {
        dataSource = context.getBean(DataSource.class);
    }

    public int add(Transaction transaction) {
        final var sql = "insert into transaction values(?, ?, ?, ?)";

        try (final var connection = dataSource.getConnection();
             final var statement = connection.prepareStatement(sql)) {

            statement.setObject(1, transaction.id());
            statement.setString(2, transaction.wallet().getUsername());
            statement.setString(3, transaction.direction().name());
            statement.setBigDecimal(4, transaction.amount());

            return statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while adding TransactionRequest", e);
            return 0;
        }
    }

    public synchronized boolean exists(Transaction transaction) {
        final var sql = "select id from transaction where id = ? order by created_at desc limit 1000";

        try (final var connection = dataSource.getConnection();
             final var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, transaction.id());

            return statement.executeQuery().next();
        } catch (SQLException e) {
            log.error("Error checking existence of transaction", e);
            throw new IllegalArgumentException(e);
        }
    }
}
