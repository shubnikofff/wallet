package org.company.client.server;

import org.company.client.configuration.ApplicationConfiguration;
import org.company.client.service.TransactionRequestExecutor;
import org.company.client.service.TransactionRequestPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);

    private static ApplicationServer instance;

    private final ApplicationContext context;

    private ApplicationServer(ApplicationContext context) {
        this.context = context;
    }

    public static ApplicationServer getInstance() {
        if (instance == null) {
            log.info("Initializing server");
            try {
                final var configuration = ApplicationConfiguration.read("/configuration.yaml");
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
        context.getBean(TransactionRequestExecutor.class).execute();
        log.info("Server started");
    }

    public void shutdown() {
        log.info("Stopping server");

        try {
            context.getBean(TransactionRequestExecutor.class).stop();
            context.getBean(TransactionRequestPublisher.class).disconnect();
        } catch (Exception e) {
            log.error("Unable stop server", e);
            throw new RuntimeException(e);
        }

        log.info("Server stopped");
    }
}
