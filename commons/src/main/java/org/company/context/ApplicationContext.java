package org.company.context;

public interface ApplicationContext {

    <T> T getBean(Class<T> type);
}
