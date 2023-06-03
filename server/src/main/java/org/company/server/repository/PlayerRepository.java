package org.company.server.repository;

import org.company.model.Player;
import org.company.server.model.Wallet;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlayerRepository implements Bean {

    private static final Logger log = LoggerFactory.getLogger(PlayerRepository.class);

    private DataSource dataSource;

    @Override
    public void init(ApplicationContext context) {
        dataSource = context.getBean(DataSource.class);
    }

    public List<Player> findAll() {
        final var sql = "select username, balance_version, balance from player";

        try (final var connection = dataSource.getConnection();
             final var statement = connection.prepareStatement(sql)) {

            final var resultSet = statement.executeQuery();
            final var all = new ArrayList<Player>();

            while (resultSet.next()) {
                all.add(player(resultSet));
            }

            return all;
        } catch (SQLException e) {
            log.error("Error while searching Player", e);
            return Collections.emptyList();
        }
    }

    public Optional<Player> findByUsername(String username) {
        final var sql = "select username, balance_version, balance from player where username = ?";

        try (final var connection = dataSource.getConnection();
             final var statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            final var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(player(resultSet));
            }

            return Optional.empty();
        } catch (SQLException e) {
            log.error("Error while searching Player", e);
            return Optional.empty();
        }
    }

    public int add(Player player) {
        final var sql = "insert into player values(?, ?, ?)";

        try (final var connection = dataSource.getConnection();
             final var statement = connection.prepareStatement(sql)) {

            statement.setString(1, player.username());
            statement.setLong(2, player.balanceVersion());
            statement.setBigDecimal(3, player.balance());

            return statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while adding Player", e);
            return 0;
        }
    }

    public int updateBalance(Wallet wallet) {
        final var sql = "update player set balance_version = ?, balance = ? where username = ?";

        try (final var connection = dataSource.getConnection();
             final var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, wallet.getBalanceVersion());
            statement.setBigDecimal(2, wallet.getBalance());
            statement.setString(3, wallet.getUsername());

            return statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error while updating Player", e);
            return 0;
        }
    }

    private static Player player(ResultSet resultSet) throws SQLException {
        return new Player(
            resultSet.getString("username"),
            resultSet.getLong("balance_version"),
            resultSet.getBigDecimal("balance")
        );
    }
}
