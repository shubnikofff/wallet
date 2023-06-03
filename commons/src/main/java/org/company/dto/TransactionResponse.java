package org.company.dto;

import org.company.model.ErrorCode;
import org.company.model.TransactionDirection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

public record TransactionResponse(
    UUID transactionId,
    String username,
    ErrorCode errorCode,
    TransactionDirection transactionDirection,
    BigDecimal amount,
    BigInteger balanceVersion,
    BigDecimal newBalance
) {
}
