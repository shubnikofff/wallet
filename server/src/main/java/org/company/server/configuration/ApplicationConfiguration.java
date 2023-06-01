package org.company.server.configuration;

import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ApplicationConfiguration implements Bean {

    private BigDecimal transactionLimit;
    private List<String> playerBlacklist;

    private DatabaseConfiguration db;

    private KafkaConfiguration kafka;

    private int workerCount;

    private int port;

    public BigDecimal getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(BigDecimal transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public List<String> getPlayerBlacklist() {
        return playerBlacklist;
    }

    public void setPlayerBlacklist(List<String> playerBlacklist) {
        this.playerBlacklist = playerBlacklist;
    }

    public DatabaseConfiguration getDb() {
        return db;
    }

    public void setDb(DatabaseConfiguration db) {
        this.db = db;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public KafkaConfiguration getKafka() {
        return kafka;
    }

    public void setKafka(KafkaConfiguration kafka) {
        this.kafka = kafka;
    }

    public static ApplicationConfiguration read(String path) throws IOException {
        try (final var inputStream = ApplicationConfiguration.class.getResourceAsStream(path)) {
            final var yaml = new Yaml(new Constructor(ApplicationConfiguration.class, new LoaderOptions()));
            return yaml.load(inputStream);
        }
    }

    @Override
    public void init(ApplicationContext context) {
    }
}
