package org.company.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction (
    UUID id,
    TransactionDirection direction,
    BigDecimal amount,
    Wallet wallet
)
{}
