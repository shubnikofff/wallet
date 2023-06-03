package org.company.server.service;

import org.company.server.messaging.MessagePublisher;
import org.company.server.model.Transaction;
import org.company.server.configuration.ApplicationConfiguration;
import org.company.dto.TransactionRequest;
import org.company.dto.TransactionResponse;
import org.company.model.ErrorCode;
import org.company.server.repository.TransactionRepository;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

import static org.company.model.TransactionDirection.OUT;

public class TransactionProcessor implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionProcessor.class);

    private TransactionRepository transactionRepository;

    private MessagePublisher messagePublisher;

    private WalletManager walletManager;

    private List<String> playerBlacklist;

    private BigDecimal transactionLimit;

    @Override
    public void init(ApplicationContext context) {
        transactionRepository = context.getBean(TransactionRepository.class);
        walletManager = context.getBean(WalletManager.class);
        playerBlacklist = context.getBean(ApplicationConfiguration.class).getPlayerBlacklist();
        transactionLimit = context.getBean(ApplicationConfiguration.class).getTransactionLimit();
        messagePublisher = context.getBean(MessagePublisher.class);
    }

    public void process(TransactionRequest transactionRequest) {
        log.info("Process {}", transactionRequest);

        final var transaction = new Transaction(
            transactionRequest.id(),
            transactionRequest.direction(),
            transactionRequest.amount(),
            transactionRequest.username()
        );

        final var errorCode = check(transaction);

        if (errorCode == null) {
            walletManager.applyTransaction(transaction);
            transactionRepository.add(transaction);
        }

        publishTransactionResponse(transaction, errorCode);
    }

    private ErrorCode check(Transaction transaction) {
        if (playerBlacklist.contains(transaction.username())) {
            return ErrorCode.OPERATION_DENIED;
        }

        if (transaction.amount().compareTo(transactionLimit) > 0) {
            return ErrorCode.AMOUNT_OUT_OF_LIMIT;
        }

        final var currentBalance = walletManager.getWallet(transaction.username()).balance();
        if(transaction.direction() == OUT && currentBalance.subtract(transaction.amount()).compareTo(BigDecimal.ZERO) < 0) {
            return ErrorCode.INSUFFICIENT_FUNDS;
        }

        if (transactionRepository.exists(transaction)) {
            return ErrorCode.DUPLICATED_TRANSACTION;
        }

        return null;
    }

    private void publishTransactionResponse(Transaction transaction, ErrorCode errorCode) {
        final var wallet = walletManager.getWallet(transaction.username());
        final var response = new TransactionResponse(
            transaction.id(),
            transaction.username(),
            errorCode,
            transaction.direction(),
            transaction.amount(),
            wallet.version(),
            wallet.balance()
        );

        messagePublisher.publish(response);
    }
}
