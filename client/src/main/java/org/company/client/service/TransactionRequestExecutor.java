package org.company.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.client.messaging.TransactionRequestPublisher;
import org.company.client.util.RandomGenerator;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.TransactionRequest;
import org.company.model.Player;
import org.company.util.UncountableNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionRequestExecutor implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionRequestExecutor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private String walletServerUrl;

    private long delayBetweenRequestsMillis;

    private BigDecimal generatedAmountMaximum;

    private TransactionRequestPublisher publisher;

    private final List<ExecutorService> executors = new ArrayList<>();

    @Override
    public void init(ApplicationContext context) {
        final var configuration = context.getBean(ApplicationConfiguration.class);
        walletServerUrl = configuration.getWalletServerUrl();
        delayBetweenRequestsMillis = configuration.getTransaction().getDelayBetweenRequestsMillis();
        generatedAmountMaximum = configuration.getTransaction().getGeneratedAmountMaximum();
        publisher = context.getBean(TransactionRequestPublisher.class);
    }

    public void start() {
        final var httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(walletServerUrl + "/player"))
            .build();

        httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(this::parseResponseBody)
            .exceptionally(exception -> {
                log.error("Fail to fetch players from server", exception);
                return Collections.emptyList();
            })
            .join()
            .forEach(this::executeTransactionRequestWorker);
    }

    public void executeTransactionRequestWorker(Player player) {
        final var executor = Executors.newSingleThreadScheduledExecutor(new UncountableNamedThreadFactory(player.username()));
        executors.add(executor);

        log.info("Execute transaction request worker for player {}", player.username());

        executor.scheduleAtFixedRate(
            () -> publisher.publish(new TransactionRequest(
                UUID.randomUUID(),
                player.username(),
                RandomGenerator.direction(),
                RandomGenerator.amount(BigDecimal.ONE, generatedAmountMaximum))
            ),
            100L,
            delayBetweenRequestsMillis,
            TimeUnit.MILLISECONDS
        );
    }

    public void stop() {
        log.info("Stopping transaction request executor");
        executors.forEach(ExecutorService::shutdown);
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
