package com.restaurante.app.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Bridge that exposes the Spring {@link ApplicationContext} to Swing views.
 *
 * <p>Swing windows are created with {@code new} throughout the code base and therefore cannot receive
 * dependencies through constructor injection. Instead of instantiating controllers and DAOs directly,
 * views resolve them from the container via {@link #getBean(Class)}, so Spring still owns their
 * creation and wiring. Beans reached this way should be defined as prototypes when they hold a
 * short-lived resource (e.g. a JDBC connection).
 */
@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * Resolves a bean by type from the application context.
     *
     * @param beanType the required bean type
     * @param <T>      the bean type
     * @return the bean instance managed by Spring
     */
    public static <T> T getBean(Class<T> beanType) {
        return context.getBean(beanType);
    }
}
