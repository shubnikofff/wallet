package org.company.context;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected AbstractApplicationContext() {}

    private final Map<Type, Bean> beans = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        return (T) beans.get(type);
    }

    protected void register(Bean bean) {
        bean.init(this);
        beans.put(bean.getClass(), bean);
    }
}
