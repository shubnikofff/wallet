package org.company.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Player(
    UUID id,
    String username,
    long balanceVersion,
    BigDecimal balance
) {
}
