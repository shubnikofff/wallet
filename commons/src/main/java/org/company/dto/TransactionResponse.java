package org.company.dto;

import org.company.model.ErrorCode;
import org.company.model.TransactionDirection;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(
    UUID transactionId,
    ErrorCode errorCode,
    long balanceVersion,
    TransactionDirection transactionDirection,
    BigDecimal amount,
    BigDecimal newBalance
) {
}
