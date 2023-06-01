package org.company.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.client.messaging.TransactionRequestPublisher;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.model.Player;
import org.company.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRequestExecutor implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionRequestExecutor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private String walletServerUrl;

    private TransactionRequestPublisher publisher;
    private ExecutorService executorService;

    @Override
    public void init(ApplicationContext context) {
        final var configuration = context.getBean(ApplicationConfiguration.class);
        walletServerUrl = configuration.getWalletServerUrl();
        publisher = context.getBean(TransactionRequestPublisher.class);
        executorService = Executors.newFixedThreadPool(
            configuration.getTransaction().getRequestPublisherCount(),
            new NamedThreadFactory("transaction-request-executor")
        );
    }

    public void start() {
        final var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(walletServerUrl + "/player"))
            .build();

        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(this::parseResponseBody)
            .join()
            .forEach(this::executeTransactionRequestWorker);
    }

    public void executeTransactionRequestWorker(Player player) {
        executorService.execute(() -> {
            while (!executorService.isShutdown() || !executorService.isTerminated()) {
                try {
                    publisher.publish(TransactionRequestGenerator.request(player));
                    Thread.sleep(300);
                } catch (Exception e) {
                    log.error("Error while publishing transaction request", e);
                }
            }
        });
    }

    public void stop() {
        if (executorService != null) {
            log.info("Stopping transaction request executor");
            executorService.shutdown();
        }
    }

    private List<Player> parseResponseBody(HttpResponse<String> response) {
        try {
            return objectMapper.readValue(response.body(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Unable to parse response body: {}", response.body(), e);
            return Collections.emptyList();
        }
    }
}
