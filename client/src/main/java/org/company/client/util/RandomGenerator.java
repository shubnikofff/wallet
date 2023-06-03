package org.company.client.util;

import org.company.model.TransactionDirection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class RandomGenerator {

    private static final int SCALE = 2;

    private RandomGenerator() {}

    private static final Random random = new Random();

    public static TransactionDirection direction() {
        final var values = TransactionDirection.values();
        return values[random.nextInt(values.length)];
    }

    public static BigDecimal amount(BigDecimal min, BigDecimal max) {
        return min.add(BigDecimal.valueOf(random.nextDouble()).multiply(max.subtract(min)))
            .setScale(SCALE, RoundingMode.HALF_UP);
    }
}
