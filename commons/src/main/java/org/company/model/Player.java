package org.company.model;

import java.math.BigDecimal;


public record Player(
    String username,
    long balanceVersion,
    BigDecimal balance
) {
}
