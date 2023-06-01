package org.company.server.server;

import org.company.server.configuration.ApplicationConfiguration;
import org.company.server.repository.DataSource;
import org.company.server.service.BalanceDumpJob;
import org.company.server.service.TransactionConsumer;
import org.company.server.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);

    private static ApplicationServer instance;

    private final ApplicationContext context;

    private ApplicationServer(ApplicationContext context) {
        this.context = context;
    }

    public void start() {
        log.info("Starting server");
        context.getBean(ServletContainer.class).start();
        context.getBean(TransactionConsumer.class).start();
        context.getBean(BalanceDumpJob.class).start();
        log.info("Server started");
    }

    public static ApplicationServer instance() {
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

    public void shutdown() {
        log.info("Stopping server");

        context.getBean(DataSource.class).close();
        context.getBean(ServletContainer.class).stop();
        context.getBean(TransactionConsumer.class).stop();
        context.getBean(BalanceDumpJob.class).stop();

        log.info("Server stopped");
    }
}
