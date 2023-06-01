package org.company.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.client.messaging.TransactionRequestPublisher;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TransactionRequestExecutor implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionRequestExecutor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String walletServerUrl;

    private TransactionRequestPublisher publisher;

    private List<TransactionRequestGenerator> workers;

    @Override
    public void init(ApplicationContext context) {
        walletServerUrl = context.getBean(ApplicationConfiguration.class).getWalletServerUrl();
        publisher = context.getBean(TransactionRequestPublisher.class);
    }

    public void execute() {
        final var httpClient = HttpClient.newHttpClient();
        final var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(walletServerUrl + "/player"))
            .build();

        workers = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(this::parseResponseBody)
            .thenApply(players -> players.stream()
                .map(player -> new TransactionRequestGenerator(publisher, player))
                .toList()
            )
            .join();

        workers.forEach(Thread::start);

        for (TransactionRequestGenerator worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                log.error("Error while stopping worker.", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        Optional.ofNullable(workers)
            .orElse(new ArrayList<>())
            .forEach(Thread::interrupt);
    }

    private List<Player> parseResponseBody(HttpResponse<String> response) {
        try {
            return objectMapper.readValue(response.body(), new TypeReference<List<Player>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Unable to parse response body: {}", response.body(), e);
            return Collections.emptyList();
        }
    }
}
