package org.company.server;

import org.company.application.Environment;
import org.company.server.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        final var environment = args.length > 0 && "docker".equals(args[0]) ? Environment.DOCKER : Environment.LOCALHOST;
        final var server = ApplicationServer.instance(environment);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown, "shutdown-hook"));

        server.start();
    }
}
