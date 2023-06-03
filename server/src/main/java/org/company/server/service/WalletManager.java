package org.company.server.service;


import org.company.server.model.Transaction;
import org.company.server.model.Wallet;
import org.company.model.TransactionDirection;
import org.company.context.ApplicationContext;
import org.company.context.Bean;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WalletManager implements Bean {

    private final Map<String, Wallet> wallets = new ConcurrentHashMap<>();

    private PlayerService playerService;

    @Override
    public void init(ApplicationContext context) {
        playerService = context.getBean(PlayerService.class);
    }

    public Wallet getWallet(String username) {
        return wallets.computeIfAbsent(username, name -> playerService.get(name)
            .map(player -> new Wallet(username, player.balanceVersion(), player.balance()))
            .orElseThrow(() -> new IllegalArgumentException("Player with username " + username + " not found"))
        );
    }

    public Collection<Wallet> getAllWallets() {
        return wallets.values();
    }

    public synchronized void applyTransaction(Transaction transaction) {
        final var wallet = getWallet(transaction.username());
        final var newBalance = transaction.direction() == TransactionDirection.IN
            ? wallet.balance().add(transaction.amount())
            : wallet.balance().subtract(transaction.amount());

        wallets.put(transaction.username(), new Wallet(
            transaction.username(),
            wallet.version().add(BigInteger.ONE),
            newBalance
        ));
    }
}
