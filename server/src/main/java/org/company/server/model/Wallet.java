package org.company.server.model;

import java.math.BigDecimal;
import java.math.BigInteger;


public record Wallet(
    String username,
    BigInteger version,
    BigDecimal balance
) {
}
