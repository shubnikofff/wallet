package org.company.server;

import org.company.server.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {

        final var server = ApplicationServer.instance();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown, "shutdown-hook"));

        server.start();
    }
}
