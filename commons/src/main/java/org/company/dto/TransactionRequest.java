package org.company.dto;

import org.company.model.TransactionDirection;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
    UUID id,
    String username,
    TransactionDirection direction,
    BigDecimal amount
) {}
