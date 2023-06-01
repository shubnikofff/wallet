package org.company.server.service;

import org.company.server.model.Wallet;
import org.company.server.repository.PlayerRepository;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.util.UncountableNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BalanceDumpJob implements Bean {

    private static final Logger log = LoggerFactory.getLogger(BalanceDumpJob.class);

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new UncountableNamedThreadFactory("balance-dump-job"));

    private WalletManager walletManager;

    private PlayerRepository playerRepository;

    @Override
    public void init(ApplicationContext context) {
        walletManager = context.getBean(WalletManager.class);
        playerRepository = context.getBean(PlayerRepository.class);
    }

    public void start() {
        log.info("Starting balance dump job");
        executorService.scheduleAtFixedRate(() -> walletManager.getAllWallets().forEach(this::dump), 5, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        log.info("Stopping balance dump job");
        executorService.shutdown();
    }

    private void dump(Wallet wallet) {
        log.info("Dumping wallet: {}", wallet);
        playerRepository.updateBalance(wallet);
    }
}
