package org.company.service;


import org.company.model.Transaction;
import org.company.model.TransactionDirection;
import org.company.model.Wallet;
import org.company.context.ApplicationContext;
import org.company.context.Bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WalletManager implements Bean {

    private final Map<String, Wallet> wallets = new HashMap<>();

    private PlayerService playerService;

    @Override
    public void init(ApplicationContext context) {
        playerService = context.getBean(PlayerService.class);
    }

    public Wallet getWallet(String address) {
        return wallets.computeIfAbsent(address, username -> playerService.get(username)
            .map(player -> new Wallet(address, player.balanceVersion(), player.balance()))
            .orElseThrow(() -> new IllegalArgumentException("Player with username " + address + " not found")));
    }

    public Collection<Wallet> getAllWallets() {
        return wallets.values();
    }

    public synchronized void applyTransaction(Transaction transaction) {
        final var wallet = transaction.wallet();
        final var newBalance = transaction.direction() == TransactionDirection.IN
            ? wallet.getBalance().add(transaction.amount())
            : wallet.getBalance().subtract(transaction.amount());

        wallet.setBalance(newBalance);
        wallet.setBalanceVersion(wallet.getBalanceVersion() + 1);
    }
}
