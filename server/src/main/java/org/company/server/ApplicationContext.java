package org.company.server;

import org.company.configuration.Configuration;
import org.company.context.AbstractApplicationContext;
import org.company.repository.DataSource;
import org.company.repository.PlayerRepository;
import org.company.repository.TransactionRepository;
import org.company.service.*;
import org.company.servlet.PlayerServlet;
import org.company.servlet.ServletContainer;

public class ApplicationContext extends AbstractApplicationContext {

    private ApplicationContext() {
    }

    public static ApplicationContext init(Configuration configuration) {
        final var context = new ApplicationContext();

        context.register(configuration);
        context.register(new DataSource());
        context.register(new PlayerRepository());
        context.register(new TransactionRepository());
        context.register(new PlayerService());
        context.register(new PlayerServlet());
        context.register(new ServletContainer());
        context.register(new WalletManager());
        context.register(new TransactionResponsePublisher());
        context.register(new TransactionProcessor());
        context.register(new TransactionConsumer());
        context.register(new BalanceDumpJob());

        return context;
    }
}
