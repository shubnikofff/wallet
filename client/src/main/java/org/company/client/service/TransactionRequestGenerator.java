package org.company.client.service;

import org.company.dto.TransactionRequest;
import org.company.model.Player;
import org.company.model.TransactionDirection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.UUID;

public class TransactionRequestGenerator {

    private TransactionRequestGenerator() {}

    private static final Random random = new Random();

    public static TransactionRequest request(Player player) {
        return new TransactionRequest(
            UUID.randomUUID(),
            player.username(),
            direction(),
            amount()
        );
    }

    private static TransactionDirection direction() {
        final var values = TransactionDirection.values();
        return values[random.nextInt(values.length)];
    }

    private static BigDecimal amount() {
        final var scale = 2; // Number of decimal places
        final var precision = 3; // Number of integer places
        return BigDecimal.valueOf(random.nextDouble() * Math.pow(10, precision))
            .setScale(scale, RoundingMode.HALF_UP);
    }
}
