package org.company.client.server;

import org.company.client.configuration.ApplicationConfiguration;
import org.company.client.service.TransactionRequestExecutor;
import org.company.client.messaging.TransactionRequestPublisher;
import org.company.client.messaging.TransactionResponseConsumer;
import org.company.context.AbstractApplicationContext;

public class ApplicationContext extends AbstractApplicationContext {

    private ApplicationContext() {
    }

    public static ApplicationContext init(ApplicationConfiguration configuration) {
        final var context = new ApplicationContext();

        context.register(configuration);
        context.register(new TransactionRequestPublisher());
        context.register(new TransactionRequestExecutor());
        context.register(new TransactionResponseConsumer());

        return context;
    }
}
