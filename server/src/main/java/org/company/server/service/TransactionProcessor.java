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

        final var wallet = walletManager.getWallet(transactionRequest.username());
        final var transaction = new Transaction(
            transactionRequest.id(),
            transactionRequest.direction(),
            transactionRequest.amount(),
            wallet
        );

        final var errorCode = check(transaction);

        if (errorCode == null) {
            walletManager.applyTransaction(transaction);
            transactionRepository.add(transaction);
        }

        messagePublisher.publish(transactionResponse(transaction, errorCode));
    }

    private ErrorCode check(Transaction transaction) {
        if (playerBlacklist.contains(transaction.wallet().getUsername())) {
            return ErrorCode.OPERATION_DENIED;
        }

        if (transaction.amount().compareTo(transactionLimit) > 0) {
            return ErrorCode.AMOUNT_OUT_OF_LIMIT;
        }

        if(transaction.direction() == OUT && transaction.wallet().getBalance().subtract(transaction.amount()).compareTo(BigDecimal.ZERO) < 0) {
            return ErrorCode.INSUFFICIENT_FUNDS;
        }

        if (transactionRepository.exists(transaction)) {
            return ErrorCode.DUPLICATED_TRANSACTION;
        }

        return null;
    }

    private static TransactionResponse transactionResponse(Transaction transaction, ErrorCode errorCode) {
        return new TransactionResponse(
            transaction.id(),
            errorCode,
            transaction.wallet().getBalanceVersion(),
            transaction.direction(),
            transaction.amount(),
            transaction.wallet().getBalance()
        );
    }
}
