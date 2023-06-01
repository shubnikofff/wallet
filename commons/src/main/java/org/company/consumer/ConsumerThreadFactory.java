package org.company.consumer;

import java.util.concurrent.ThreadFactory;

public class ConsumerThreadFactory implements ThreadFactory {

    private final String namePrefix;

    private int threadCount;

    public ConsumerThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, namePrefix + "-" + ++threadCount);
    }
}
