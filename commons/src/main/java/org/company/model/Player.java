package org.company.model;

import java.math.BigDecimal;
import java.math.BigInteger;


public record Player(
    String username,
    BigInteger balanceVersion,
    BigDecimal balance
) {
}
