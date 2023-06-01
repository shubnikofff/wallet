package org.company;


import org.company.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {

        final var server = ApplicationServer.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown, "shutdown-hook"));

        server.start();
    }
}
