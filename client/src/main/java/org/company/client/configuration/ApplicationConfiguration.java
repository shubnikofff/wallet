package org.company.client.configuration;

import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;

public class ApplicationConfiguration implements Bean {

    private String walletServerUrl;

    private KafkaConfiguration kafka;

    private TransactionConfiguration transaction;

    public String getWalletServerUrl() {
        return walletServerUrl;
    }

    public void setWalletServerUrl(String walletServerUrl) {
        this.walletServerUrl = walletServerUrl;
    }

    public KafkaConfiguration getKafka() {
        return kafka;
    }

    public void setKafka(KafkaConfiguration kafka) {
        this.kafka = kafka;
    }

    public TransactionConfiguration getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionConfiguration transaction) {
        this.transaction = transaction;
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
