package org.company.client.configuration;

import java.math.BigDecimal;

public class TransactionConfiguration {

    private int requestPublisherCount;
    private int responseConsumerCount;
    private BigDecimal generatedAmountMaximum;

    public int getRequestPublisherCount() {
        return requestPublisherCount;
    }

    public void setRequestPublisherCount(int requestPublisherCount) {
        this.requestPublisherCount = requestPublisherCount;
    }

    public int getResponseConsumerCount() {
        return responseConsumerCount;
    }

    public void setResponseConsumerCount(int responseConsumerCount) {
        this.responseConsumerCount = responseConsumerCount;
    }

    public BigDecimal getGeneratedAmountMaximum() {
        return generatedAmountMaximum;
    }

    public void setGeneratedAmountMaximum(BigDecimal generatedAmountMaximum) {
        this.generatedAmountMaximum = generatedAmountMaximum;
    }
}
