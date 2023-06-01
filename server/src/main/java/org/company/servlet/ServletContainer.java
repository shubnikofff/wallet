package org.company.servlet;

import org.company.configuration.Configuration;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer implements Bean {

    private static final Logger log = LoggerFactory.getLogger(ServletContainer.class);

    private Server server;

    @Override
    public void init(ApplicationContext context) {
        final var configuration = context.getBean(Configuration.class);
        server = new Server(configuration.getPort());
        final var servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        final var playerServlet = new ServletHolder("player-servlet", context.getBean(PlayerServlet.class));
        servletHandler.addServletWithMapping(playerServlet, "/player");
    }

    public void start() {
        log.info("Starting servlet container");

        try {
            server.start();
        } catch (Exception e) {
            log.error("Unable start Jetty server", e);
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        log.info("Stopping servlet container");

        try {
            server.stop();
        } catch (Exception e) {
            log.error("Unable stop Jetty server", e);
            throw new RuntimeException(e);
        }
    }
}
