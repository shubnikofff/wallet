package org.company.server.model;

import org.company.model.TransactionDirection;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction (
    UUID id,
    TransactionDirection direction,
    BigDecimal amount,
    String username
)
{}
