package org.company.util;

import java.util.concurrent.ThreadFactory;

public class UncountableNamedThreadFactory implements ThreadFactory {

    private final String name;

    public UncountableNamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, name);
    }
}
