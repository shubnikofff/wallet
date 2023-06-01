package org.company.server.server;

import org.company.server.configuration.ApplicationConfiguration;
import org.company.server.repository.DataSource;
import org.company.server.repository.PlayerRepository;
import org.company.server.repository.TransactionRepository;
import org.company.server.service.*;
import org.company.context.AbstractApplicationContext;
import org.company.server.servlet.PlayerServlet;
import org.company.server.servlet.ServletContainer;

public class ApplicationContext extends AbstractApplicationContext {

    private ApplicationContext() {
    }

    public static ApplicationContext init(ApplicationConfiguration configuration) {
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
