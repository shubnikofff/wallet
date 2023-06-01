package org.company.client.service;

import org.company.model.Player;
import org.company.dto.TransactionRequest;
import org.company.model.TransactionDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;

public class TransactionRequestGenerator extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TransactionRequestGenerator.class);

    private static final Random random = new Random();

    private final TransactionRequestPublisher publisher;

    private final Player player;

    public TransactionRequestGenerator(TransactionRequestPublisher publisher, Player player) {
        super(player.username());
        this.publisher = publisher;
        this.player = player;
    }

    @Override
    public void run() {
        boolean isRunning = true;
        while (isRunning) {
            try {
                final var transaction = generateTransactionRequest();
                log.info("Publishing transaction: {}", transaction);
                publisher.publish(transaction, player);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.info("Stopping worker");
                isRunning = false;
            }
        }
    }

    private TransactionRequest generateTransactionRequest() {
        return new TransactionRequest(
            UUID.randomUUID(),
            player.username(),
            generateTransactionDirection(),
            generateTransactionAmount()
        );
    }

    private static TransactionDirection generateTransactionDirection() {
        final var values = TransactionDirection.values();
        return values[random.nextInt(values.length)];
    }

    private static BigDecimal generateTransactionAmount() {
        final var scale = 2; // Number of decimal places
        final var precision = 3; // Number of integer places
        return BigDecimal.valueOf(random.nextDouble() * Math.pow(10, precision))
            .setScale(scale, RoundingMode.HALF_UP);
    }
}
