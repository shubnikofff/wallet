package org.company.client.server;

import org.company.application.Environment;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.client.messaging.PlayerEventConsumer;
import org.company.client.service.TransactionRequestExecutor;
import org.company.client.messaging.TransactionRequestPublisher;
import org.company.client.messaging.TransactionResponseConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);

    private static ApplicationServer instance;

    private final ApplicationContext context;

    private ApplicationServer(ApplicationContext context) {
        this.context = context;
    }

    public static ApplicationServer instance(Environment environment) {
        if (instance == null) {
            log.info("Initializing server");
            try {
                final var configurationPath = environment == Environment.DOCKER ? "/configuration.yaml" : "/configuration.local.yaml";
                final var configuration = ApplicationConfiguration.read(configurationPath);
                final var context = ApplicationContext.init(configuration);
                instance = new ApplicationServer(context);
            } catch (Exception e) {
                log.error("Cannot instantiate Application Server", e);
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    public void start() {
        log.info("Starting server");
        context.getBean(TransactionResponseConsumer.class).start();
        context.getBean(TransactionRequestExecutor.class).start();
        context.getBean(PlayerEventConsumer.class).start();
        log.info("Server started");
    }

    public void shutdown() {
        log.info("Stopping server");

        try {
            context.getBean(TransactionRequestExecutor.class).stop();
            context.getBean(TransactionResponseConsumer.class).stop();
            context.getBean(TransactionRequestPublisher.class).disconnect();
            context.getBean(PlayerEventConsumer.class).stop();
        } catch (Exception e) {
            log.error("Unable stop server", e);
            throw new RuntimeException(e);
        }

        log.info("Server stopped");
    }
}
