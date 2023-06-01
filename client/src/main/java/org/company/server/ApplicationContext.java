package org.company.server;

import org.company.configuration.ApplicationConfiguration;
import org.company.context.AbstractApplicationContext;
import org.company.service.TransactionRequestPublisher;
import org.company.service.TransactionRequestExecutor;

public class ApplicationContext extends AbstractApplicationContext {

    private ApplicationContext() {
    }

    public static ApplicationContext init(ApplicationConfiguration configuration) {
        final var context = new ApplicationContext();

        context.register(configuration);
        context.register(new TransactionRequestPublisher());
        context.register(new TransactionRequestExecutor());

        return context;
    }
}
