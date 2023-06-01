package org.company.server.service;

import org.company.dto.PlayerCreatedEvent;
import org.company.model.Player;
import org.company.server.messaging.MessagePublisher;
import org.company.server.repository.PlayerRepository;
import org.company.server.servlet.dto.CreatePlayerRequest;
import org.company.context.ApplicationContext;
import org.company.context.Bean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerService implements Bean {

    private PlayerRepository playerRepository;

    private MessagePublisher messagePublisher;

    @Override
    public void init(ApplicationContext context) {
        playerRepository = context.getBean(PlayerRepository.class);
        messagePublisher = context.getBean(MessagePublisher.class);
    }

    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    public Optional<Player> get(String username) {
        return playerRepository.findByUsername(username);
    }

    public Player create(CreatePlayerRequest request) {
        final var player = new Player(UUID.randomUUID(), request.username(), 1L, BigDecimal.ZERO);
        final var newRecordsCount = playerRepository.add(player);

        if(newRecordsCount == 1) {
            messagePublisher.publish(new PlayerCreatedEvent(player));
            return player;
        }

        return null;
    }
}
